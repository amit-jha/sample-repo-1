package com.aj.searchapi.providers;

import com.aj.searchapi.util.SearchResult;
import com.aj.searchapi.util.UQO;

public interface Adapter {

    SearchResult search(UQO uqo);

}
