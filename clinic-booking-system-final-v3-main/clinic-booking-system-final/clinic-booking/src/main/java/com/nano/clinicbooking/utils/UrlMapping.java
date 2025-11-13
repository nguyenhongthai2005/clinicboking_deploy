package com.nano.clinicbooking.utils;

public class UrlMapping {
    public static final String API ="/api/v1";
    public static final String USERS = API + "/users";
    public static final String UPDATE_USER = "/update/{userId}";
    public static final String REGISTER_USER = "/register";
    public static final String GET_USER_BY_ID = "/user/{userId}";
    public static final String DELETE_USER_BY_ID = "/delete/{userId}";
    public static final String GET_ALL_USER = "/all-users";




    public static final String APPOINTMENT = API + "/appointments";
    public static final String CREATE_APPOINTMENT = "/create";
    public static final String GET_ALL_APPOINTMENTS = "/all";
    public static final String GET_APPOINTMENT_BY_ID = "/{appointmentId}";
    public static final String GET_APPOINTMENT_BY_STATUS = "/{status}";
    public static final String UPDATE_APPOINTMENT = "/update/{appointmentId}";
    public static final String DELETE_APPOINTMENT = "/delete/{appointmentId}";
}


