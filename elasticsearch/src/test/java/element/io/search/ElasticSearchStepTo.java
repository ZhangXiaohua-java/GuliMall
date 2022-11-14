package element.io.search;

import com.alibaba.fastjson.JSON;
import element.io.search.domain.Account;
import element.io.search.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.RangeQueryBuilder;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

/**
 * @author 张晓华
 * @date 2022-11-10
 */
@RunWith(SpringRunner.class)
@Slf4j
@SpringBootTest
public class ElasticSearchStepTo {

	@Resource
	private RestHighLevelClient client;


	@Test
	public void query() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("java");
		SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource().query(QueryBuilders.matchAllQuery());
		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		long millis = searchResponse.getTook().getMillis();
		log.info("执行用时{}毫秒", millis);
		SearchHit[] searchHits = searchResponse.getHits().getHits();
		for (SearchHit searchHit : searchHits) {
			String source = searchHit.getSourceAsString();
			User user = JSON.parseObject(source, User.class);
			log.info("获取到的信息{}", user);
		}
	}

	@Test
	public void queryByCondition() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("newbank");
		SearchSourceBuilder searchSourceBuilder = SearchSourceBuilder.searchSource()
				.query(QueryBuilders.matchQuery("address", "Kings"));
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		long millis = searchResponse.getTook().getMillis();
		log.info("执行耗时{}毫秒", millis);
		for (SearchHit hit : searchResponse.getHits().getHits()) {
			String source = hit.getSourceAsString();
			Account account = JSON.parseObject(source, Account.class);
			log.info("查询到的结果{}", account);
		}

	}


	/**
	 * 分页查询 SearchSourceBuilder对象的form,size方法传递指定的参数即可
	 *
	 * @throws IOException
	 */
	@Test
	public void queryForPage() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		searchRequest.indices("newbank");
		SearchSourceBuilder sourceBuilder = SearchSourceBuilder.searchSource()
				.query(QueryBuilders.matchQuery("gender", "M"));
		// 分页
		sourceBuilder.from(0);
		sourceBuilder.size(30);
		// 指定要查询的属性字段,相当于select xxx,xxx ... ,可以减少网络传输的负担.
		sourceBuilder.fetchSource(new String[]{"balance"}, new String[]{});
		sourceBuilder.sort("balance", SortOrder.DESC);
		searchRequest.source(sourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		long millis = searchResponse.getTook().getMillis();
		log.info("执行用时{}毫秒", millis);
		SearchHit[] hits = searchResponse.getHits().getHits();
		for (SearchHit hit : hits) {
			String sourceAsString = hit.getSourceAsString();
			Account account = JSON.parseObject(sourceAsString, Account.class);
			log.info("查询到的结果{}", account);
		}
	}


	/**
	 * GET newbank/_search
	 * {
	 * "query": {
	 * "bool": {
	 * "must": [
	 * {
	 * "match": {
	 * "gender": "M"
	 * }
	 * }
	 * ],
	 * "must_not": [
	 * {
	 * "range": {
	 * "age": {
	 * "gt": 20,
	 * "lt": 30
	 * }
	 * }
	 * }
	 * ]
	 * }
	 * }
	 * }
	 */
	@Test
	public void boolQuery() throws IOException {
		SearchRequest searchRequest = new SearchRequest();

		BoolQueryBuilder boolQueryBuilder = QueryBuilders.boolQuery();
		boolQueryBuilder.must(QueryBuilders.matchQuery("gender", "M"));
		boolQueryBuilder.mustNot(QueryBuilders.rangeQuery("age").gt("20").lt("30"));
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// 构建复合查询条件
		searchSourceBuilder.query(boolQueryBuilder);
		// 哥的眼里只有钱
		searchSourceBuilder.fetchSource(new String[]{"balance"}, new String[]{});
		searchSourceBuilder.sort("balance", SortOrder.DESC);
		searchRequest.source(searchSourceBuilder);
		searchRequest.indices("newbank");
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] hits = searchResponse.getHits().getHits();
		for (SearchHit hit : hits) {
			String source = hit.getSourceAsString();
			Account account = JSON.parseObject(source, Account.class);
			log.info("查询到的数据{}", account);
		}
		long millis = searchResponse.getTook().getMillis();
		log.info("执行耗时{}毫秒", millis);
	}


	@Test
	public void rangeQuery() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		RangeQueryBuilder rangeQueryBuilder = QueryBuilders.rangeQuery("balance").gt(10000L).lt(15000L);
		searchSourceBuilder.query(rangeQueryBuilder);
		searchSourceBuilder.sort("balance", SortOrder.DESC);
		searchSourceBuilder.fetchSource(new String[]{"balance"}, new String[]{});
		searchSourceBuilder.from(0);
		searchSourceBuilder.size(9);
		searchRequest.source(searchSourceBuilder);
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		SearchHit[] hits = searchResponse.getHits().getHits();
		for (SearchHit hit : hits) {
			String source = hit.getSourceAsString();
			Account account = JSON.parseObject(source, Account.class);
			log.info("查询到的数据{}", account);
		}
		long millis = searchResponse.getTook().getMillis();
		log.info("执行用时{}毫秒", millis);
		float score = searchResponse.getHits().getMaxScore();
		// 数值太小,Nan
		log.info("最大得分{}", score * 100);
	}

	
	@Test
	public void aggs() throws IOException {
		SearchRequest searchRequest = new SearchRequest();
		SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		searchRequest.indices("newbank");
		TermsAggregationBuilder aggs = AggregationBuilders.terms("count_gender_num").field("gender").size(2)
				.subAggregation(AggregationBuilders.terms("per_gender_num")
						.field("age").size(100));
		searchSourceBuilder.aggregation(aggs);
		// 不需要检索数据
		searchSourceBuilder.size(0);
		searchRequest.source(searchSourceBuilder);
		log.info("构造出的DSL语句{}", JSON.toJSONString(searchRequest));
		SearchResponse searchResponse = client.search(searchRequest, RequestOptions.DEFAULT);
		Aggregations aggregations = searchResponse.getAggregations();
		Terms aggregation = aggregations.get("count_gender_num");
		List<? extends Terms.Bucket> buckets = aggregation.getBuckets();
		for (Terms.Bucket bucket : buckets) {
			log.info("每一个桶key的名字{},统计数量{}", bucket.getKey(), bucket.getDocCount());
			Terms agg = bucket.getAggregations().get("per_gender_num");
			// 子聚合需要通过bucket才能获取到子聚合信息
			for (Terms.Bucket aggBucket : agg.getBuckets()) {
				log.info("子聚合的key{},统计数量{}", aggBucket.getKey(), aggBucket.getDocCount());
			}
		}
	}


}
