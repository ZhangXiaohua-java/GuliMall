package element.io.search.service.impl;

import com.alibaba.fastjson.JSON;
import element.io.mall.common.to.SkuEsModelTo;
import element.io.search.service.SearchService;
import element.io.search.vo.SearchParamVo;
import element.io.search.vo.SearchResultVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.apache.lucene.search.join.ScoreMode;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.NestedQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.nested.NestedAggregationBuilder;
import org.elasticsearch.search.aggregations.bucket.nested.ParsedNested;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedLongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.ParsedStringTerms;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.aggregations.bucket.terms.TermsAggregationBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static element.io.mall.common.enumerations.ElasticSearchConstant.PRODUCT_INDEX;

/**
 * @author 张晓华
 * @date 2022-11-17
 */
@SuppressWarnings({"all"})
@Slf4j
@Service
public class SearchServiceImpl implements SearchService {

	private static final Integer HAS_STOCK = 1;

	private static final Integer PAGE_SIZE = 2;

	@Resource
	private RestHighLevelClient restHighLevelClient;


	@Override
	public SearchResultVo searchProducts(SearchParamVo searchParamVo) throws IOException {
		SearchRequest searchRequest = buildRequest(searchParamVo);
		SearchResponse searchResponse = restHighLevelClient.search(searchRequest, RequestOptions.DEFAULT);
		return getSearchResultVo(searchResponse, searchParamVo);
	}


	private SearchRequest buildRequest(SearchParamVo param) {
		log.info("接收到的参数{}", param);
		SearchSourceBuilder sourceBuilder = new SearchSourceBuilder();
		BoolQueryBuilder boolQueryCondition = QueryBuilders.boolQuery();
		// 查询条件
		if (StringUtils.hasText(param.getKeyword())) {
			boolQueryCondition = QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("skuTitle", param.getKeyword()));
			HighlightBuilder highlightBuilder = new HighlightBuilder()
					.field("skuTitle")
					.preTags("<b style='color:red;'>")
					.postTags("</b>");
			sourceBuilder.highlighter(highlightBuilder);
		}
		// 组装Filter条件
		if (!CollectionUtils.isEmpty(param.getBrandId())) {
			boolQueryCondition.filter(QueryBuilders.termsQuery("brandId", param.getBrandId()));
		}
		if (StringUtils.hasText(param.getSkuPrice())) {
			String priceStr = param.getSkuPrice();
			String[] split = priceStr.split("_");
			// 三种组合形式
			//	 0_5000 指定区间
			//	 _8000 小于这个价格
			//	 8000_ 必须大于这个价格
			if (priceStr.startsWith("_")) {
				boolQueryCondition.filter(QueryBuilders.rangeQuery("skuPrice").lte(split[0]));
			} else if (priceStr.endsWith("_")) {
				boolQueryCondition.filter(QueryBuilders.rangeQuery("skuPrice").gte(split[0]));
			} else {
				boolQueryCondition.filter(QueryBuilders.rangeQuery("skuPrice").gte(split[0]).lte(split[1]));
			}
		}
		if (Objects.nonNull(param.getCatalog3Id())) {
			boolQueryCondition.filter(QueryBuilders.termQuery("catalogId", param.getCatalog3Id()));
		}
		if (!CollectionUtils.isEmpty(param.getAttrs())) {
			List<String> attrs = param.getAttrs();
			for (String attr : attrs) {
				if (attr.equals("") || !attr.contains("_")) {
					continue;
				}
				String[] attrItem = attr.split("_");
				String attrId = attrItem[0];
				QueryBuilder attrQueryCondition = QueryBuilders.termsQuery("attrs.attrId", attrId);
				NestedQueryBuilder nestedQueryBuilder = QueryBuilders.nestedQuery("attrs", attrQueryCondition, ScoreMode.None);
				boolQueryCondition.filter(nestedQueryBuilder);
				if (attrItem.length > 1) {
					String attrValue = attrItem[1];
					String[] attrValues = attrValue.split(":");
					if (!CollectionUtils.isEmpty(Arrays.asList(attrValues))) {
						boolQueryCondition.filter(QueryBuilders.nestedQuery("attrs", QueryBuilders.termsQuery("attrs.attrValue", attrValues), ScoreMode.None));
					}
				}
			}
		}
		// 没有选择这个条件就不拼接了,即展示所有的商品信息
		if (Objects.nonNull(param.getHasStock()) && param.getHasStock().equals(HAS_STOCK)) {
			boolQueryCondition.filter(QueryBuilders.termQuery("hasStock", true));
		}
		try (FileOutputStream fileOutputStream = new FileOutputStream(new File("D://condition.json"))) {
			fileOutputStream.write(sourceBuilder.toString().getBytes(StandardCharsets.UTF_8));
		} catch (Exception e) {
			e.printStackTrace();
		}
		// 组装排序信息
		if (StringUtils.hasText(param.getSort()) && param.getSort().contains("_")) {
			String sort = param.getSort();
			String[] splits = sort.split("_");
			String field = splits[0];
			String sortOrder = splits[1];
			sourceBuilder.sort(field, sortOrder.equals("asc") ? SortOrder.ASC : SortOrder.DESC);
		}

		// 组装分页信息
		Integer pageNum = 1;
		if (Objects.nonNull(param.getPageNum())) {
			pageNum = param.getPageNum();
		}
		sourceBuilder.from((pageNum - 1) * PAGE_SIZE);
		sourceBuilder.size(PAGE_SIZE);
		TermsAggregationBuilder brandAgg = AggregationBuilders.terms("brand_agg").field("brandId").size(10)
				.subAggregation(AggregationBuilders.terms("brand_name_agg")
						.field("brandName").size(10)
						.subAggregation(AggregationBuilders.terms("brand_img_agg")
								.field("brandImg").size(10)));
		NestedAggregationBuilder attrAgg = AggregationBuilders.nested("attr_agg", "attrs").subAggregation(AggregationBuilders.terms("attr_id_agg").field("attrs.attrId")
				.size(10).subAggregation(AggregationBuilders.terms("attr_name_agg")
						.field("attrs.attrName").size(10).subAggregation(AggregationBuilders.terms("attr_value_agg")
								.field("attr.attrValue").size(10))));
		TermsAggregationBuilder catalogAgg = AggregationBuilders.terms("catalog_agg").field("catalogId").size(10)
				.subAggregation(AggregationBuilders.terms("catalog_name_agg").field("catalogName")
						.size(10));
		sourceBuilder.aggregation(brandAgg);
		sourceBuilder.aggregation(attrAgg);
		sourceBuilder.aggregation(catalogAgg);
		sourceBuilder.query(boolQueryCondition);
		log.info("组装的查询条件: {}", sourceBuilder.toString());
		return new SearchRequest().indices(PRODUCT_INDEX).source(sourceBuilder);
	}


	private SearchResultVo getSearchResultVo(SearchResponse searchResponse, SearchParamVo param) {
		SearchResultVo result = new SearchResultVo();
		long total = searchResponse.getHits().getTotalHits().value;
		result.setPageNum(Objects.nonNull(param.getPageNum()) ? param.getPageNum().longValue() : 1);
		long totalPages = Double.valueOf(Math.ceil(((double) total / PAGE_SIZE))).longValue();
		result.setTotalPages(totalPages);
		ArrayList<SkuEsModelTo> models = new ArrayList<>();
		SearchHit[] hits = searchResponse.getHits().getHits();
		for (SearchHit hit : hits) {
			String source = hit.getSourceAsString();
			SkuEsModelTo model = JSON.parseObject(source, SkuEsModelTo.class);
			models.add(model);
		}
		result.setProducts(models);
		Aggregations agg = searchResponse.getAggregations();
		ParsedNested attrAgg = agg.get("attr_agg");
		ParsedLongTerms attrIdAgg = attrAgg.getAggregations().get("attr_id_agg");
		ArrayList<SearchResultVo.Attr> attrs = new ArrayList<>();
		for (Terms.Bucket bucket : attrIdAgg.getBuckets()) {
			SearchResultVo.Attr attr = new SearchResultVo.Attr();
			long attrId = bucket.getKeyAsNumber().longValue();
			String attrName = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getKeyAsString();
			ParsedStringTerms attrValueAgg = ((ParsedStringTerms) bucket.getAggregations().get("attr_name_agg")).getBuckets().get(0).getAggregations().get("attr_value_agg");
			ArrayList<String> attrValues = new ArrayList<>();
			for (Terms.Bucket attrValueAggBucket : attrValueAgg.getBuckets()) {
				String attrValue = attrValueAggBucket.getKeyAsString();
				attrValues.add(attrValue);
			}
			attr.setAttrId(attrId);
			attr.setAttrName(attrName);
			attr.setAttrValue(attrValues);
			attrs.add(attr);
		}
		Terms brand_agg = agg.get("brand_agg");
		ArrayList<SearchResultVo.Brand> brands = new ArrayList<>();
		for (Terms.Bucket bucket : brand_agg.getBuckets()) {
			long brandId = bucket.getKeyAsNumber().longValue();
			ParsedStringTerms brandNameAgg = bucket.getAggregations().get("brand_name_agg");
			// 因为是嵌套的关系,所以可以确定只有一个子聚合
			String brandName = brandNameAgg.getBuckets().get(0).getKeyAsString();
			Terms brandImgAgg = brandNameAgg.getBuckets().get(0).getAggregations().get("brand_img_agg");
			String brandImg = brandImgAgg.getBuckets().get(0).getKeyAsString();
			SearchResultVo.Brand brand = new SearchResultVo.Brand();
			brand.setBrandId(brandId);
			brand.setBrandName(brandName);
			brand.setBrandImg(brandImg);
			brands.add(brand);
		}
		ParsedLongTerms catalogAgg = agg.get("catalog_agg");
		ArrayList<SearchResultVo.Catalog> catalogs = new ArrayList<>();
		for (Terms.Bucket bucket : catalogAgg.getBuckets()) {
			long catalogId = bucket.getKeyAsNumber().longValue();
			ParsedStringTerms catalogNameAgg = bucket.getAggregations().get("catalog_name_agg");
			String catalogName = catalogNameAgg.getBuckets().get(0).getKeyAsString();
			SearchResultVo.Catalog catalog = new SearchResultVo.Catalog();
			catalog.setCatalogId(catalogId);
			catalog.setCatalogName(catalogName);
			catalogs.add(catalog);
		}
		result.setAttrs(attrs);
		result.setBrands(brands);
		result.setCatalogs(catalogs);
		log.info("查询到的结果{}", result);
		try {
			StringBuffer sb = new StringBuffer(JSON.toJSONString(result));
			FileUtils.write(new File("D://result.json"), sb, Charset.forName("utf-8"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new SearchResultVo();
	}


}
