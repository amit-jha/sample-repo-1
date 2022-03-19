package com.aj.searchapi.util;

import lombok.Builder;
import lombok.Getter;

/**
 * The UQO is a unified query object.
 * It defines a contract between application and different search providers.
 * It is different from UQL
 *  1. It can have derived fields based on certain business
 *  2. Provide business encapsulation
 *  3. Uniform to all providers
 */

@Getter
public class UQO {

    private String q;
    private int page;

    public UQO(UQL uql) {
        this.q = uql.getKeyword();
        this.page = uql.getPage();
    }

    public UQO(Builder builder) {
        this.q = builder.q;
        this.page = builder.page;
    }

    public static Builder builder(){
        return new UQO.Builder();
    }

    public static class Builder{
        private String q;
        private int page;

        public UQO.Builder q(String q){
            this.q = q;
            return this;
        }

        public UQO.Builder page(int page){
            this.page = page;
            return this;
        }

        public UQO build(){
            return new UQO(this);
        }

    }





}
