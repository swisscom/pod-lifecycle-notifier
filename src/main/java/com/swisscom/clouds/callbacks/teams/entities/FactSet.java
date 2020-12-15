package com.swisscom.clouds.callbacks.teams.entities;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class FactSet implements Section {

    private List<Fact> facts = new ArrayList<>();

}
