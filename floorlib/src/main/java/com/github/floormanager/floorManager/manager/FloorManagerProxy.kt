package com.github.floormanager.floorManager.manager

/**
 * 楼层管理器代理类
 * 负责多模块的FloorManager实例管理和创建
 */

class FloorManagerProxy {

    companion object {

        //存储每个模块的FloorManager
        private val managerMap: MutableMap<String, FloorManager> = HashMap(3)


        /**
         * 获取每个模块的FloorManager实例
         * 如果之前从未获取过则进行创建
         * 
         * @param moduleName 模块名称
         * @return FloorManager实例
         */

        @Synchronized
        fun getInstances(moduleName: String): FloorManager {
            var manager: FloorManager? = null
            if (managerMap.isNotEmpty()) {
                manager = managerMap[moduleName]
            }
            if (manager == null) {
                manager = FloorManager()
                managerMap[moduleName] = manager
            }
            return manager
        }
    }


}