package com.example.instaapiintegration.model;

public class Image {
    private Standard_Resolution standard_resolution;
    public  Standard_Resolution getStandard_resolution(){
        return standard_resolution;
    }

    public class Standard_Resolution{
        private String url;
        public String getUrl(){
            return  url;
        }
    }
}
