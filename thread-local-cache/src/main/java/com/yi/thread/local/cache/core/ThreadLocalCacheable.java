package com.yi.thread.local.cache.core;

import java.util.List;

public interface ThreadLocalCacheable<ID, R> {

    ID resolveKey(R result);

    List<R> cacheableGet(List<ID> ids);
}
