package element.io.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import element.io.mall.common.annotation.ShowStatusCheck;
import element.io.mall.common.validation.AppendGroup;
import element.io.mall.common.validation.ModifyGroup;
import lombok.Data;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.URL;

import javax.validation.constraints.*;
import java.io.Serializable;

/**
 * 品牌
 *
 * @author 张晓华
 * @email 3323393308@qq.com
 * @date 2022-10-27 20:11:30
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "必须指定要修改的项目的ID", groups = {ModifyGroup.class})
	@Null(message = "新增项目不允许指定ID", groups = {AppendGroup.class})
	@TableId
	private Long brandId;

	/**
	 * 品牌名
	 */
	@NotBlank(message = "必须指定项目名", groups = {AppendGroup.class})
	@Pattern(regexp = "[\u4e00-\u9fa5]{2,}", groups = {AppendGroup.class, ModifyGroup.class})
	private String name;
	/**
	 * 品牌logo地址
	 */
	@NotBlank(message = "logo必须提交", groups = {AppendGroup.class})
	@URL(message = "logo的值必须是一个合法的URL地址", groups = {AppendGroup.class, ModifyGroup.class})
	private String logo;
	/**
	 * 介绍
	 */
	@NotBlank(message = "描述内容不能为空", groups = {AppendGroup.class})
	@Length(min = 6, message = "描述的内容至少六位", groups = {AppendGroup.class, ModifyGroup.class})
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@ShowStatusCheck(enumVals = {0, 1}, groups = {AppendGroup.class, ModifyGroup.class}, message = "showStatus只能是指定范围的值")
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotBlank(message = "新增的项目的检索首字母不允许为空", groups = {AppendGroup.class})
	@Pattern(regexp = "[a-zA-Z]", groups = {AppendGroup.class, ModifyGroup.class}, message = "提交的检索首字母必须是单个字母")
	private String firstLetter;
	/**
	 * 排序
	 */
	@Min(value = 0, groups = {AppendGroup.class, ModifyGroup.class}, message = "排序字段的值必须是大于0的整数")
	private Integer sort;

}
