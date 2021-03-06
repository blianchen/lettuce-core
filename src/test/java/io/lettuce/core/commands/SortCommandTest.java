/*
 * Copyright 2011-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lettuce.core.commands;

import static io.lettuce.core.SortArgs.Builder.*;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import io.lettuce.core.AbstractRedisClientTest;
import io.lettuce.core.ListStreamingAdapter;

/**
 * @author Will Glozer
 * @author Mark Paluch
 */
public class SortCommandTest extends AbstractRedisClientTest {
    @Test
    public void sort() {
        redis.rpush(key, "3", "2", "1");
        assertThat(redis.sort(key)).isEqualTo(list("1", "2", "3"));
        assertThat(redis.sort(key, asc())).isEqualTo(list("1", "2", "3"));
    }

    @Test
    public void sortStreaming() {
        redis.rpush(key, "3", "2", "1");

        ListStreamingAdapter<String> streamingAdapter = new ListStreamingAdapter<String>();
        Long count = redis.sort(streamingAdapter, key);

        assertThat(count.longValue()).isEqualTo(3);
        assertThat(streamingAdapter.getList()).isEqualTo(list("1", "2", "3"));
        streamingAdapter.getList().clear();

        count = redis.sort(streamingAdapter, key, desc());
        assertThat(count.longValue()).isEqualTo(3);
        assertThat(streamingAdapter.getList()).isEqualTo(list("3", "2", "1"));
    }

    @Test
    public void sortAlpha() {
        redis.rpush(key, "A", "B", "C");
        assertThat(redis.sort(key, alpha().desc())).isEqualTo(list("C", "B", "A"));
    }

    @Test
    public void sortBy() {
        redis.rpush(key, "foo", "bar", "baz");
        redis.set("weight_foo", "8");
        redis.set("weight_bar", "4");
        redis.set("weight_baz", "2");
        assertThat(redis.sort(key, by("weight_*"))).isEqualTo(list("baz", "bar", "foo"));
    }

    @Test
    public void sortDesc() {
        redis.rpush(key, "1", "2", "3");
        assertThat(redis.sort(key, desc())).isEqualTo(list("3", "2", "1"));
    }

    @Test
    public void sortGet() {
        redis.rpush(key, "1", "2");
        redis.set("obj_1", "foo");
        redis.set("obj_2", "bar");
        assertThat(redis.sort(key, get("obj_*"))).isEqualTo(list("foo", "bar"));
    }

    @Test
    public void sortLimit() {
        redis.rpush(key, "3", "2", "1");
        assertThat(redis.sort(key, limit(1, 2))).isEqualTo(list("2", "3"));
    }

    @Test
    public void sortStore() {
        redis.rpush("one", "1", "2", "3");
        assertThat(redis.sortStore("one", desc(), "two")).isEqualTo(3);
        assertThat(redis.lrange("two", 0, -1)).isEqualTo(list("3", "2", "1"));
    }
}
