package com.swisscom.clouds.callbacks.teams.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class PotentialAction {

    private final String name;
    @JsonProperty("@type")
    private String type = "OpenUri";
    private List<Target> targets = new ArrayList<>();
}