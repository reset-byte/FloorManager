package com.github.floormanager.pdFloor.entity

import com.google.gson.annotations.SerializedName

/**
 * 属性组Data，完全照搬阿波罗商详主接口中的接口定义，具体说明详见主接口文档
 *
 */

class EntitySceneColorSize(
    @SerializedName("C-P#dimension&dimension") var group: EntitySceneGroup,
    @SerializedName("buttonPartList") var buttons: ArrayList<EntitySceneButton> = ArrayList()
) {

    class EntitySceneButtonContent(
        @SerializedName("text") var text: String = "",
        @SerializedName("skuList") var skuList: ArrayList<String> = ArrayList(),
        @SerializedName("serialNumber") var serialNumber: Int = 0,
        @SerializedName("stockState") var stockState: Int = 0,
        @SerializedName("imgUrl") var imgUrl: String? = ""
    ) {
        var isEnable = true
        var groupIndex: Int = 0

        fun hasStock(): Boolean {
            return stockState != 34
        }

    }

    class EntitySceneButton(
        @SerializedName("C-P#dimensionButton&dimensionButton") var buttonContent: EntitySceneButtonContent
    )

    class EntitySceneGroup(
        @SerializedName("dimTitle") var text: String = "",
        @SerializedName("dimensionNumber") var index: Int = 0
    )

}