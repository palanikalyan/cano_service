package com.dfpt.canonical.controller;

import com.dfpt.canonical.dto.ExternalTradeDTO;
import com.dfpt.canonical.model.CanonicalTrade;
import com.dfpt.canonical.repository.CanonicalTradeRepository;
import com.dfpt.canonical.service.FileLoaderService;
import com.dfpt.canonical.service.MapperService;
import com.dfpt.canonical.service.OutboxService;
import com.dfpt.canonical.service.QueuePublisherService;
import com.dfpt.canonical.service.ValidatorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileReader;
import java.util.List;

@RestController
@RequestMapping("/canonical")
@Tag(name = "Canonical Service", description = "APIs for processing trade files in multiple formats")
public class CanonicalController {

    @Autowired
    private FileLoaderService fileLoaderService;

    @Autowired
    private MapperService mapperService;

    @Autowired
    private ValidatorService validatorService;

    @Autowired
    private CanonicalTradeRepository tradeRepo;

    @Autowired
    private OutboxService outboxService;

    @Autowired
    private QueuePublisherService queuePublisherService;

    @PostMapping("/process/{format}")
    @Operation(
        summary = "Process trade file",
        description = "Processes a trade file in the specified format (json, xml, or csv), validates it, saves to database, creates an outbox event, and publishes to ActiveMQ queue"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully processed trade file"),
        @ApiResponse(responseCode = "400", description = "Invalid format or validation failed"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public String process(
            @Parameter(description = "File format: json, xml, or csv", required = true)
            @PathVariable String format) throws Exception {

        File file = fileLoaderService.load("orders." + format);

        ExternalTradeDTO dto = null;

        switch (format) {
            case "json":
                ObjectMapper objectMapper = new ObjectMapper();
                dto = objectMapper.readValue(file, ExternalTradeDTO.class);
                break;

            case "xml":
                XmlMapper xmlMapper = new XmlMapper();
                dto = xmlMapper.readValue(file, ExternalTradeDTO.class);
                break;

            case "csv":
                try (FileReader reader = new FileReader(file)) {
                    CsvToBean<ExternalTradeDTO> csvToBean = new CsvToBeanBuilder<ExternalTradeDTO>(reader)
                            .withType(ExternalTradeDTO.class)
                            .withIgnoreLeadingWhiteSpace(true)
                            .build();

                    List<ExternalTradeDTO> dtos = csvToBean.parse();
                    if (!dtos.isEmpty()) {
                        dto = dtos.get(0);
                    }
                }
                break;

            default:
                throw new RuntimeException("Unsupported format: " + format);
        }

        if (dto == null) {
            throw new RuntimeException("Failed to parse file");
        }

        CanonicalTrade trade = mapperService.mapFromJson(dto);
        validatorService.validate(trade);

        tradeRepo.save(trade);
        outboxService.create(trade);
        
        // Publish to ActiveMQ Queue
        queuePublisherService.publishToQueue(trade);

        return "SUCCESS - Canonical object created with ID: " + trade.getId() + " and published to ActiveMQ queue";
    }
}
