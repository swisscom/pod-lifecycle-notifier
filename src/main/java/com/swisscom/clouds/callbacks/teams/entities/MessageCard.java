package com.swisscom.clouds.callbacks.teams.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class MessageCard {

    private final String title;

    @JsonProperty("@context")
    private String context = "https://schema.org/extensions";

    @JsonProperty("@type")
    private String type = "MessageCard";

    private String themeColor = Severity.INFO.getCode();
    private String text;

    private List<PotentialAction> potentialAction = new ArrayList<>();
    private List<Section> sections = new ArrayList<>();
}
