package com.backend.api.common;

public class AccessToken {

    private static final String token_two =
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2OTEwNTE5OTMsImV4cCI6MTY5MTA1Mzc5MywiZW1haWwiOiJob2lsZGF5NTMwM0BuYXZlci5jb20iLCJyb2xlIjoiTUVNQkVSIn0.V9V4IsH36grO3VfF76xgATvSedyKXMcKJuH6UggIdcVG4_HKkDMg2PVRJurCct56N_9eeJGfRi4zxP9evsk1wA";
    private static final String token_one =
            "Br_DgZX1wBQXR1oeynNvWR1_6DinK03DQ1SRGfeqCisM1AAAAYm6jSPA";
    private static final String token =
    "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJBQ0NFU1MiLCJpYXQiOjE2OTEwNTEzNDksImV4cCI6MTY5MTA1MzE0OSwiZW1haWwiOiJob2lsZGF5NTMwM0BuYXZlci5jb20iLCJyb2xlIjoiTUVNQkVSIn0.8C0hP7TGDCDmYXSB8MecWh2KMn4oOMn5kgFHeqZHPhNN3bLT94HE-dTIXqvABecspOhrNA_qnlTUA8fDNY2Qfg";
    private static final String testEmail =
            "hoilday5303@naver.com";
    public static String getToken() {
        return token;
    }

    public static String getToken_two() {return token_two;}

    public static String getTestEmail() {
        return testEmail;
    }
}
