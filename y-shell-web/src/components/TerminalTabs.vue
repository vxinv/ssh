<template>
    <el-tabs v-model="editableTabsValue" type="card" @tab-remove="removeTab" @tab-click="switchTab">
        <el-tab-pane
                v-loading="loading"
                v-for="(item, index) in editableTabs"
                :key="item.name"
                :label="item.title"
                :name="item.name"
                :index="index"
                :closable="item.closable"
        >
            <LatestUsed v-if="item.name === '1'"></LatestUsed>
            <BaseTerm v-if="item.name !== '1'" :ref="item.name" :divId="item.divId" :node="item.node" @changeLoading="changeLoading"></BaseTerm>
        </el-tab-pane>
    </el-tabs>
</template>

<script>
import BaseTerm from '../components/BaseTerm';
import LatestUsed from '../components/LatestUsed';

export default {
    name: "TerminalTabs",
    components: {
        BaseTerm: BaseTerm,
        LatestUsed: LatestUsed,
    },
    data() {
        return {
            loading: false,
            editableTabsValue: '1',
            editableTabs: [{
                title: '工作台',
                name: '1',
                closable: false,
                divId: 'terminal_p',
            }],
            tabIndex: 1,
            // 激活的标签
            activeName: 1,
        }
    },
    methods: {
        addTab(node) {
            let newTabName = ++this.tabIndex + '';
            this.editableTabs.push({
                title: node.name + '-' + node.config.host,
                name: newTabName,
                closable: true,
                divId: "terminal_" + newTabName,
                node: node,
            });
            this.editableTabsValue = newTabName;
            this.activeName = newTabName;
            this.loading = true
        },
        removeTab(targetName) {
            let tabs = this.editableTabs;
            let activeName = this.editableTabsValue;
            if (activeName === targetName) {
                tabs.forEach((tab, index) => {
                    if (tab.name === targetName) {
                        let nextTab = tabs[index + 1] || tabs[index - 1];
                        if (nextTab) {
                            activeName = nextTab.name;
                        }
                    }
                });
            }
            this.editableTabsValue = activeName;
            this.activeName = activeName;
            this.editableTabs = tabs.filter(tab => tab.name !== targetName);
        },
        switchTab(tabs) {
            // 更改当前选中的选项卡
            this.activeName = tabs.name;
        },
        changeLoading(){
            //console.log("changeLoading","链接成功")
            this.loading = false
        }
    },
    mounted() {

    }
}
</script>

<style scoped>

</style>