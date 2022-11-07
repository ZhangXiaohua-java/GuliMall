package element.io.mall.product.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import element.io.mall.common.util.PageUtils;
import element.io.mall.product.dao.SpuImagesDao;
import element.io.mall.product.entity.SpuImagesEntity;
import element.io.mall.product.service.SpuImagesService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Service("spuImagesService")
public class SpuImagesServiceImpl extends ServiceImpl<SpuImagesDao, SpuImagesEntity> implements SpuImagesService {

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		return null;
	}

	@Override
	public void saveImages(Long id, List<String> images) {
		if (CollectionUtils.isEmpty(images)) {
			return;
		}
		List<SpuImagesEntity> imagesEntities = images.stream().map(img -> {
			SpuImagesEntity spuImagesEntity = new SpuImagesEntity();
			spuImagesEntity.setSpuId(id);
			spuImagesEntity.setImgUrl(img);
			return spuImagesEntity;
		}).collect(Collectors.toList());
		this.saveBatch(imagesEntities);
	}
	
}