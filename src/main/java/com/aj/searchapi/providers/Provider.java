package com.aj.searchapi.providers;

import com.aj.searchapi.exception.ApplicationException;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//INFO: Record & switch expression is used.
public record Provider(String code, String name, boolean active){
//    public Provider {
//        adapter = switch (code){
//            case "NY" -> new NewYorkTimesSearchAdapter();
//            case "TG" -> new TheGuardianSearchAdapter();
//            default -> null;
//        };
//    }
}