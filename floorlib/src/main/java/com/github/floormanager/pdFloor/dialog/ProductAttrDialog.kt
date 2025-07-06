package com.github.floormanager.pdFloor.dialog

import android.content.DialogInterface
import android.graphics.Paint
import android.os.Bundle
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.DialogFragment
import com.github.floormanager.pdFloor.utils.*
import com.github.floormanager.widget.FlowLayout
import com.github.floormanager.floorlib.R
import com.github.floormanager.floorlib.databinding.PdBaseFloorDialogAttrLayoutBinding
import com.github.floormanager.pdFloor.entity.EntityDialogProductAttrData
import com.github.floormanager.pdFloor.entity.EntitySceneColorSize
import com.github.floormanager.utils.ImageLoader
import com.github.floormanager.utils.PriceFormatUtils
import com.github.floormanager.utils.IsvPriceFormatUtils
import com.github.floormanager.utils.dp2px
import java.math.BigDecimal

/**
 * 通用商品属性浮层对话框
 * 负责展示商品属性选择界面，支持属性选择、价格显示和购买操作
 * 
 * @param isProductDetail 是否来自商详页
 * @param data 渲染所用数据
 * @param customViews 添加到内容视图底部的自定义View集合
 */

class ProductAttrDialog(
    private val isProductDetail: Boolean = false,
    private var data: EntityDialogProductAttrData,
    private val customViews: ArrayList<Pair<View, LinearLayout.LayoutParams>> = ArrayList()
) : DialogFragment(),
    View.OnClickListener {

    init {
        //防crash 不可去除
    }

    private val mainImageCornerRadius = 4f.dp2px
    private val mainImageSize = 100f.dp2px

    private var callBack: CallBack? = null
    private var _binding: PdBaseFloorDialogAttrLayoutBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = PdBaseFloorDialogAttrLayoutBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        initView()
        showData()
        binding.scrollView.post {
            binding.scrollView.scrollTo(0, 0)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun initView() {
        binding.originalPriceText.paint.flags =
            binding.originalPriceText.paint.flags or Paint.STRIKE_THRU_TEXT_FLAG

        binding.countLayout.visibility = if (isProductDetail) View.VISIBLE else View.GONE
        binding.shortOfStock.visibility = if (isProductDetail) View.VISIBLE else View.GONE
        binding.addToCartBtn.visibility = if (isProductDetail) View.VISIBLE else View.GONE
        binding.buyNowBtn.visibility = if (isProductDetail) View.VISIBLE else View.GONE
        binding.okBtn.visibility = if (isProductDetail) View.GONE else View.VISIBLE

        binding.closeBtn.setOnClickListener(this)
        binding.addToCartBtn.setOnClickListener(this)
        binding.buyNowBtn.setOnClickListener(this)
        binding.okBtn.setOnClickListener(this)
        // TODO: 修复 countControl 的 setCountChangedListener 方法
        // binding.countControl.setCountChangedListener(this)

        customViews.forEach {
            if (it.first.parent == null) {
                binding.scrollContentView.addView(it.first, it.second)
            }
        }
    }

    fun setCallBack(callBack: CallBack) {
        this.callBack = callBack
    }

    fun setAttrDialogData(data: EntityDialogProductAttrData) {
        this.data = data
        if (isVisible) {
            showData()
        }
    }

    fun getAttrDialogData(): EntityDialogProductAttrData {
        return data
    }

    fun setAttrDialogPrice(price: String?) {
        data.price = price
        displayPrice()
    }

    fun setAttrDialogSelectedSkuId(selectedSkuId: String) {
        data.selectedSkuId = selectedSkuId
    }

    fun scrollToBottom() {
        binding.scrollView.scrollTo(0, binding.scrollView.height)
    }

    private fun showData() {
        displayMainImage()
        displayPrice()
        displayAttrGroup()
        displayBottomBtn()
        if (isProductDetail) {
            displayCount()
        }
        callBack?.onShowData()
    }

    private fun displayCount() {
        if (!data.isBottomBtnEnable) {
            // TODO: 修复 countControl 的方法调用
            // binding.countControl.setValueInput(data.selectedCount)
            // //如果数量大于1表示是通过数量增减触发的
            // if (data.selectedCount > 1) {
            //     binding.countControl.setMaxLimit(data.selectedCount)
            // } else {
            //     binding.countControl.setMaxLimit(1)
            //     binding.countControl.setButtonEnabled(false)
            // }
        } else {
            // TODO: 修复 countControl 的方法调用
            // binding.countControl.setMaxLimit(200)
            // binding.countControl.setValueInput(data.selectedCount)
        }
        if (!TextUtils.isEmpty(data.stockHint)) {
            binding.shortOfStock.visibility = View.VISIBLE
            binding.shortOfStock.text = data.stockHint
        } else {
            binding.shortOfStock.visibility = View.GONE
        }
    }

    private fun displayBottomBtn() {
        binding.addToCartBtn.visibility = if (!data.isShowSingleBottomBtn) View.VISIBLE else View.GONE
        binding.buyNowBtn.visibility = if (!data.isShowSingleBottomBtn) View.VISIBLE else View.GONE
        binding.okBtn.visibility = if (data.isShowSingleBottomBtn) View.VISIBLE else View.GONE
        binding.okBtn.isEnabled = data.isBottomBtnEnable
        if (data.bottomBtnText.isNotEmpty()) {
            binding.okBtn.text = data.bottomBtnText
        }
        if (data.addToCartBtnText.isNotEmpty()) {
            binding.addToCartBtn.text = data.addToCartBtnText
        }
        if (data.buyNowBtnText.isNotEmpty()) {
            binding.buyNowBtn.text = data.buyNowBtnText
        }
    }

    private fun displayMainImage() {
        if (!TextUtils.isEmpty(data.mainImageUrl)) {
            ImageLoader.loadRoundedImage(
                binding.mainImage,
                data.mainImageUrl!!,
                mainImageCornerRadius,
                mainImageSize,
                mainImageSize,
                context?.getAttrResourceId(R.attr.isv_image_placeholder) ?: ImageLoader.NOT_SET,
                context?.getAttrResourceId(R.attr.isv_image_placeholder) ?: ImageLoader.NOT_SET
            )
        }
    }

    private fun displayPrice() {
        val priceBigDecimal = PriceFormatUtils.string2BigDecimal(data.price)
        val originalPriceBigDecimal = PriceFormatUtils.string2BigDecimal(data.originalPrice)
        if (priceBigDecimal != null && priceBigDecimal >= BigDecimal.ZERO) {
            binding.priceText.visibility = View.VISIBLE
            binding.noPriceText.visibility = View.INVISIBLE
            IsvPriceFormatUtils.setTextWithPriceStyle2(binding.priceText, priceBigDecimal, true)
        } else {
            binding.priceText.visibility = View.INVISIBLE
            binding.noPriceText.visibility = View.VISIBLE
        }
        if (priceBigDecimal != null && originalPriceBigDecimal != null && priceBigDecimal < originalPriceBigDecimal) {
            binding.originalPriceText.text = PriceFormatUtils.formatToNumber(originalPriceBigDecimal, true)
        } else {
            binding.originalPriceText.text = null
        }
        binding.noPriceText.setTextColor(binding.priceText.context.getAttrColor(if (data.isSlash) R.attr.isv_color_C30 else R.attr.isv_color_C9))
        binding.priceText.setTextColor(binding.priceText.context.getAttrColor(if (data.isSlash) R.attr.isv_color_C30 else R.attr.isv_color_C9))
    }

    private fun displayAttrGroup() {

        binding.attrGroupLayout.removeAllViews()

        if (data.attrGroupData.isNullOrEmpty()) {
            return
        }

        val inflater = LayoutInflater.from(requireContext())
        for (attrGroup in data.attrGroupData!!) {
            val root: View = inflater.inflate(
                R.layout.pd_base_floor_attr_group_layout,
                binding.attrGroupLayout,
                false
            )
            val attributeGroupName = root.findViewById<TextView>(R.id.attributeGroupName)
            val attributeContainer: FlowLayout = root.findViewById(R.id.attributeContainer)
            attributeGroupName.text = attrGroup.group.text
            for (attr in attrGroup.buttons) {
                val attributeView = inflater.inflate(
                    R.layout.pd_base_floor_attr_item_text_layout,
                    attributeContainer,
                    false
                )
                val skuName = attributeView.findViewById<TextView>(R.id.skuName)
                val outOfStockTag = attributeView.findViewById<TextView>(R.id.outOfStockTag)
                skuName.text = attr.buttonContent.text
                skuName.setTextColor(
                    requireContext().getAttrColor(
                        when {
                            data.isButtonSelected(
                                attr
                            ) -> R.attr.isv_color_C9_3
                            attr.buttonContent.isEnable -> R.attr.isv_color_C7
                            else -> R.attr.isv_color_C4
                        }
                    )
                )
                outOfStockTag.visibility =
                    if (attr.buttonContent.hasStock()) View.INVISIBLE else View.VISIBLE
                outOfStockTag.isSelected = data.isButtonSelected(attr)
                skuName.isSelected = data.isButtonSelected(attr)
                if (!attr.buttonContent.isEnable) {
                    skuName.paint.flags =
                        skuName.paint.flags or Paint.STRIKE_THRU_TEXT_FLAG
                }
                skuName.setTag(R.id.pd_base_floor_dialog_product_attr_tag, attr)
                skuName.setOnClickListener(this)
                attributeContainer.addView(attributeView)
            }
            binding.attrGroupLayout.addView(root)
        }
    }

    private fun showProductMessage(msg: String) {
        // TODO: 实现 SnackBar 提示功能
        // 临时使用 Toast 替代
        android.widget.Toast.makeText(context, msg, android.widget.Toast.LENGTH_SHORT).show()
    }

    override fun onClick(v: View?) {

        val selectedSku = data.getRealSelectedSku()

        when (v?.id) {

            R.id.addToCartBtn -> {
                if (selectedSku == null) {
                    showProductMessage(
                        getString(R.string.pd_base_floor_dialog_product_select_hint)
                    )
                    return
                }
                callBack?.onAddToCartBtnClick(selectedSku)
            }

            R.id.buyNowBtn -> {
                if (selectedSku == null) {
                    showProductMessage(
                        getString(R.string.pd_base_floor_dialog_product_select_hint)
                    )
                    return
                }
                callBack?.onBuyNowBtnClick(selectedSku)
            }

            R.id.okBtn -> {
                if (selectedSku == null) {
                    showProductMessage(
                        getString(R.string.pd_base_floor_dialog_product_select_hint)
                    )
                    return
                }
                callBack?.onOkBtnClick(selectedSku)
            }

            R.id.closeBtn -> {
                dismiss()
            }

            R.id.skuName -> {
                val skuData =
                    v.getTag(R.id.pd_base_floor_dialog_product_attr_tag) as EntitySceneColorSize.EntitySceneButton

                if (!skuData.buttonContent.isEnable) {
                    return
                }

                data.onButtonClick(skuData)
                displayAttrGroup()

                val changeSkuId: String? = data.getRealSelectedSku()

                if (!TextUtils.isEmpty(changeSkuId)) {
                    callBack?.onSkuSelected(changeSkuId!!, data.realSelectedSkuHasStock())
                }
            }

        }
    }

    abstract class CallBack {

        /**
         * 渲染数据
         */

        open fun onShowData() {}

        /**
         * SKU被选中
         * 
         * @param skuId 被选中的商品skuId
         * @param hasStock 被选中的sku是否有库存
         */

        open fun onSkuSelected(skuId: String, hasStock: Boolean) {}

        /**
         * 底部确认按钮被点击
         * 
         * @param skuId 当前选中商品的skuId
         */

        open fun onOkBtnClick(skuId: String? = null) {}

        /**
         * 底部添加购物车按钮被点击
         * 
         * @param skuId 当前选中商品的skuId
         */

        open fun onAddToCartBtnClick(skuId: String? = null) {}

        /**
         * 底部立即购买按钮被点击
         * 
         * @param skuId 当前选中商品的skuId
         */

        open fun onBuyNowBtnClick(skuId: String? = null) {}

        /**
         * 商品数量改变
         * 
         * @param value 改变后的数量
         */

        open fun onCountChange(value: Int) {}

        /**
         * 弹窗被关闭
         */

        open fun onDismiss() {}

    }

    override fun onDismiss(dialog: DialogInterface) {
        customViews.forEach {
            if (it.first.parent != null) {
                binding.scrollContentView.removeView(it.first)
            }
        }
        super.onDismiss(dialog)
        callBack?.onDismiss()
    }

    // TODO: 修复 OnConfirm 接口的实现
    // override fun onConfirm(p0: Int, p1: Boolean, p2: Boolean) {
    //     if (!p1) {
    //         callBack?.onCountChange(p0)
    //     }
    // }

}