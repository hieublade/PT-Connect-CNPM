package com.ptconnect.model.entity;

import java.io.Serializable;
import java.math.BigDecimal;

public class Workout implements Serializable {
    private Long id;
    private Long planId;
    private String exerciseName;
    private String targetMuscle;
    private Integer sets;
    private Integer reps;
    private BigDecimal weightKg;
    private Integer restSecond;
    private String videoDemoUrl;

    public Workout() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPlanId() { return planId; }
    public void setPlanId(Long planId) { this.planId = planId; }

    public String getExerciseName() { return exerciseName; }
    public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }

    public String getTargetMuscle() { return targetMuscle; }
    public void setTargetMuscle(String targetMuscle) { this.targetMuscle = targetMuscle; }

    public Integer getSets() { return sets; }
    public void setSets(Integer sets) { this.sets = sets; }

    public Integer getReps() { return reps; }
    public void setReps(Integer reps) { this.reps = reps; }

    public BigDecimal getWeightKg() { return weightKg; }
    public void setWeightKg(BigDecimal weightKg) { this.weightKg = weightKg; }

    public Integer getRestSecond() { return restSecond; }
    public void setRestSecond(Integer restSecond) { this.restSecond = restSecond; }

    public String getVideoDemoUrl() { return videoDemoUrl; }
    public void setVideoDemoUrl(String videoDemoUrl) { this.videoDemoUrl = videoDemoUrl; }
}
