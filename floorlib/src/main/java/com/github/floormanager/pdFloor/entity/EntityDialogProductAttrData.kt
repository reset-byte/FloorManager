package com.github.floormanager.pdFloor.entity

/**
 *
 * 属性浮层Data
 *
 * @param mainImageUrl 主图url
 * @param price 实际售价
 * @param originalPrice 划线价
 * @param attrGroupData 属性组列表，因为内部逻辑较复杂，里面的字段定义直接使用阿波罗商详主接口下发的字段定义
 * @param selectedSkuId 当前选中的skuid
 * @param isSlash 是否是砍价商品
 *
 */

class EntityDialogProductAttrData(
    var mainImageUrl: String? = null,
    var price: String? = null,
    var originalPrice: String? = null,
    var attrGroupData: ArrayList<EntitySceneColorSize>? = null,
    selectedSkuId: String? = null,
    var isSlash: Boolean = false
) {

    var selectedSkuId = selectedSkuId
        set(value) {
            //当前已选skuId发生改变时需要对属性组区域进行整体刷新
            field = value
            if (!attrGroupData.isNullOrEmpty()) {
                setButtonSelected()
                setButtonEnable()
            }
        }

    //当前选择的商品数量
    var selectedCount: Int = 1

    //库存状态的提示文案
    var stockHint: String = ""

    //底部按钮是否展示为一个撑满一行
    var isShowSingleBottomBtn: Boolean = true

    //底部按钮是否可点击
    var isBottomBtnEnable: Boolean = true

    //撑满一行的底部按钮文案
    var bottomBtnText: String = ""

    //底部加车按钮的文案
    var addToCartBtnText: String = ""

    //底部立即购买按钮的文案
    var buyNowBtnText: String = ""

    //属性组中针对当前已选skuid，所选中的按钮的集合，key为属性组的index，value为按钮data
    private val selectedButtons = HashMap<Int?, EntitySceneColorSize.EntitySceneButton?>()

    init {
        //需要对属性组中按钮的选中和是否可点进行初始化，接口并未直接下发这些状态
        if (!attrGroupData.isNullOrEmpty()) {
            setButtonSelected()
            setButtonEnable()
        }
    }

    /**
     *
     * 属性组中按钮点击
     *
     * @param buttonData 点击按钮对应的数据
     *
     */
    fun onButtonClick(buttonData: EntitySceneColorSize.EntitySceneButton) {
        if (selectedButtons[buttonData.buttonContent.groupIndex] == buttonData) {
            //点击已经选中的按钮，则走反选逻辑，移除这一按钮所属组的选中数据
            selectedButtons[buttonData.buttonContent.groupIndex] = null
        } else {
            //点击没有选中的按钮，则选中逻辑，为这一按钮所属组添加选中按钮数据
            selectedButtons[buttonData.buttonContent.groupIndex] = buttonData
        }
        //刷新所有属性组的按钮状态
        setButtonEnable()
    }

    /**
     *
     * 判断按钮是否选中
     *
     * @param 按钮data
     * @return 按钮是否选中
     *
     */
    fun isButtonSelected(buttonData: EntitySceneColorSize.EntitySceneButton): Boolean {
        return selectedButtons.containsValue(buttonData)
    }

    /**
     *
     * 设置选中按钮的数据集合
     *
     *
     */
    private fun setButtonSelected() {
        //如果没有属性组数据则不需要往下执行逻辑
        if (attrGroupData.isNullOrEmpty()) {
            return
        }

        //先清空之前的已选数据
        selectedButtons.clear()

        //根据当前已选sku决定哪些按钮选中，先遍历属性组data
        for (i in attrGroupData!!.indices) {
            val group: EntitySceneColorSize = attrGroupData!![i]
            selectedButtons[group.group.index] = null
            //遍历组内所有按钮
            for (button in group.buttons) {
                //为按钮赋值属性组的index，为之后渲染做准备
                button.buttonContent.groupIndex = group.group.index
                if (button.buttonContent.skuList.contains(selectedSkuId)) {
                    //如果按钮对应的sku列表中有当前选择的sku，则视为选中，添加到map中
                    selectedButtons[group.group.index] = button
                }
            }
        }
    }

    /**
     *
     * 设置按钮是否可点。因为属性组的按钮之间为正交关系，并不是所有的组合都一定能对应到具体某个sku上，针对没有对应关系的按钮就需要进行
     * 置灰，此处逻辑较复杂，北汽和标准版都是一样的逻辑，应该不需要再改动了，如果确实需要改的话请谨慎。
     *
     */
    private fun setButtonEnable() {
        //如果没有属性组数据则不需要往下执行逻辑
        if (attrGroupData.isNullOrEmpty()) {
            return
        }

        //根据当前选择的按钮决定其他按钮是否置灰 这里就与当前已选sku无关了
        for (group in attrGroupData!!) {
            for (button in group.buttons) {
                if (attrGroupData!!.size == 1) {
                    //如果只有一个组则存在skuList就可点
                    button.buttonContent.isEnable = button.buttonContent.skuList.size > 0
                } else {
                    //如果这个按钮的skulist有一个能跟其他组的已选按钮组成一个确定的sku则可点
                    button.buttonContent.isEnable = false
                    A@ for (btnSkuId in button.buttonContent.skuList) {

                        B@ for (groupB in attrGroupData!!) {

                            if (groupB.group.index == button.buttonContent.groupIndex) {
                                //如果是当前按钮的这一组则不需要进行判断
                                continue@B
                            }

                            if (selectedButtons[groupB.group.index] != null) {
                                //如果这一组有已选按钮
                                if (selectedButtons[groupB.group.index]!!.buttonContent.skuList.contains(
                                        btnSkuId
                                    )
                                ) {
                                    //已选按钮的skulist里有目标sku，则继续判断下一组
                                    continue@B
                                } else {
                                    //已选按钮的skulist里没有目标sku，则该sku无法跑通，按钮置灰无需继续往下判断
                                    continue@A
                                }
                            }

                            //如果该组没有已选，即表示用户进行了反选操作，则需要判断该组的所有按钮的skulist是否存在目标sku
                            C@ for (groupButton in groupB.buttons) {
                                if (groupButton.buttonContent.skuList.contains(btnSkuId)) {
                                    //当前组遍历到的按钮skulist里存在目标sku，则该组可用，继续判断其他的组
                                    continue@B
                                } else {
                                    //当前组遍历到的按钮skulist里不存在目标sku,继续判断该组其他按钮
                                    continue@C
                                }
                            }

                            //如果走到这里代表这一组不存在目标sku匹配到的按钮，无法跑通，按钮置灰无需继续往下判断
                            continue@A

                        }

                        //走到这里代表目标sku存在可以跑通的路径，按钮可点
                        button.buttonContent.isEnable = true
                        break@A
                    }
                }
            }
        }
    }

    /**
     *
     * 当前选择的sku是否有库存
     * @return 是否有库存
     *
     */
    fun realSelectedSkuHasStock(): Boolean {
        var result = true

        for (selectedButton in selectedButtons) {
            //库存状态在主接口是按钮维度下发的，需要满足选中的所有按钮都有库存才视为有库存
            result = result && selectedButton.value?.buttonContent?.hasStock() ?: false
        }

        return result
    }

    /**
     *
     * 获取属性组中所有选中按钮对应的sku
     * @return 选中的skuId
     *
     *
     */
    fun getRealSelectedSku(): String? {

        //不存在属性信息，直接返回初始化传入的skuid
        if (attrGroupData.isNullOrEmpty()) {
            return selectedSkuId
        }

        var resultList: Set<String>? = null
        for (selectedButton in selectedButtons) {
            if (selectedButton.value == null) {
                //该按钮所在组用户进行了反选 无法获取到落地的sku
                return null
            }

            //对所有选中的按钮对应的skulist寻找交集
            resultList = if (resultList.isNullOrEmpty()) {
                selectedButton.value!!.buttonContent.skuList.toSet()
            } else {
                resultList intersect selectedButton.value!!.buttonContent.skuList
            }

        }

        //正常业务数据下交集应该只有一个sku，故只需取第一个即可
        if (!resultList.isNullOrEmpty()) {
            return resultList.first()
        }

        return null
    }

}