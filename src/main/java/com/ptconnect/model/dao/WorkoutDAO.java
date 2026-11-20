package com.ptconnect.model.dao;

import com.ptconnect.model.entity.Workout;
import java.sql.*;
import java.util.ArrayList;

public class WorkoutDAO extends DAO {

    public WorkoutDAO() { super(); }
    public WorkoutDAO(Connection conn) { super(conn); }

    public ArrayList<Workout> getWorkoutByPlan(Long planId) {
        ArrayList<Workout> list = new ArrayList<>();
        String sql = "SELECT * FROM workouts WHERE plan_id = ? ORDER BY id ASC";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, planId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Workout w = new Workout();
                        w.setId(rs.getLong("id"));
                        w.setPlanId(rs.getLong("plan_id"));
                        w.setExerciseName(rs.getString("exercise_name"));
                        w.setTargetMuscle(rs.getString("target_muscle"));
                        w.setSets(rs.getInt("sets"));
                        w.setReps(rs.getInt("reps"));
                        w.setRestSecond(rs.getInt("rest_seconds"));
                        w.setWeightKg(rs.getBigDecimal("weight_kg"));
                        w.setVideoDemoUrl(rs.getString("video_demo_url"));
                        list.add(w);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return list;
    }

    public boolean addWorkout(Workout workout) {
        String sql = "INSERT INTO workouts (plan_id, exercise_name, target_muscle, sets, reps, weight_kg, rest_seconds, video_demo_url) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, workout.getPlanId());
                ps.setString(2, workout.getExerciseName());
                ps.setString(3, workout.getTargetMuscle());
                ps.setInt(4, workout.getSets());
                ps.setInt(5, workout.getReps());
                ps.setBigDecimal(6, workout.getWeightKg());
                ps.setInt(7, workout.getRestSecond());
                ps.setString(8, workout.getVideoDemoUrl());
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }

    public boolean deleteWorkout(Long workoutId) {
        String sql = "DELETE FROM workouts WHERE id = ?";
        Connection c = null;
        try {
            c = getConnection();
            try (PreparedStatement ps = c.prepareStatement(sql)) {
                ps.setLong(1, workoutId);
                return ps.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeIfOwned(c);
        }
        return false;
    }
}
