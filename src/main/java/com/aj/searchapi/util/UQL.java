package com.aj.searchapi.util;

import lombok.Getter;

@Getter
public class UQL {

    private String keyword;
    private int page;

    private UQL(Builder builder){

        this.keyword = builder.keyword;
        this.page = builder.page;
    }

    public static class Builder{
        private String keyword;
        private int page;

        public Builder keyword(String keyword) {
            this.keyword = keyword;
            return this;
        }

        public Builder page(int page) {
            this.page = page;
            return this;
        }

        public UQL build(){
            return new UQL(this);
        }
    }

}
