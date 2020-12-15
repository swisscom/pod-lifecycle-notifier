package com.swisscom.clouds.callbacks.teams.entities;

import lombok.Data;

@Data
public class Activity implements Section {

    private final String text;
    private Boolean markdown = true;

}
