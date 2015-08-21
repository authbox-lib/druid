---
layout: doc_page
---
# Schema Changes

Schemas for datasources can change at any time and Druid supports different schemas among segments.

## Replacing Segments

Druid uniquely 
identifies segments using the datasource, interval, version, and partition number. The partition number is only present if 
there are multiple segments created for some granularity of time. For example, if you have hourly segments, but you 
have more data in an hour than a single segment can hold, you can create multiple segments for the same hour. These segments will share 
the same datasource, interval, and version, but have linearly increasing partition numbers.

```
foo_2015-01-01/2015-01-02_v1_0
foo_2015-01-01/2015-01-02_v1_1
foo_2015-01-01/2015-01-02_v1_2
```

If at some later point in time, you reindex the data with a new schema, the newly created segments will have a higher version id.

```
foo_2015-01-01/2015-01-02_v2_0
foo_2015-01-01/2015-01-02_v2_1
foo_2015-01-01/2015-01-02_v2_2
```

Druid supports atomically replacing segments on a datasource and granularity level. In our example, once all `v2` segments are 
loaded and queryable in a Druid cluster, all queries switch to getting data from `v2` segments. If the total set is incomplete, Druid 
will still query `v1` segments.

Note that replacing segments across granularities is eventually consistent. For example, you have segments such as the following:

```
foo_2015-01-01/2015-01-02_v1_0
foo_2015-01-02/2015-01-03_v1_1
foo_2015-01-03/2015-01-04_v1_2
```

`v2` segments will be loaded into the cluster as soon as they are built and replace `v1` segments for the period of time the 
segments overlap. Before v2 segments are completely loaded, your cluster may have a mixture of `v1` and `v2` segments.
 
```
foo_2015-01-01/2015-01-02_v1_0
foo_2015-01-02/2015-01-03_v2_1
foo_2015-01-03/2015-01-04_v1_2
``` 
 
In this case, queries may hit a mixture of `v1` and `v2` segments.


## Different Schemas Among Segments

Druid segments for the same datasource may have different schemas. If a string column (dimension) exists in one segment but not 
another, queries that involve both segments still work. Queries for the segment missing the dimension will behave as if the dimension is 
present and all values are null. Similarly, if one segment has a numeric column (metric) but another does not, queries on the segment missing the 
metric will behave as if the metric column is present and all values are 0.


