<template>
    <div>
        <el-row>
            <el-col :span="4">
                <el-tabs style="padding:0 5px 0 5px">
                    <el-tab-pane>
                        <span slot="label"><i class="el-icon-date"></i>会话</span>
                        <Tree :addTab="addTab"></Tree>
                    </el-tab-pane>
                    <el-tab-pane>
                        <span slot="label"><i class="el-icon-date"></i>设置</span>
                        <el-row class="set-class">
                            <el-col :span="6">
                                <el-tooltip class="item" effect="dark" content="设置终端的字体大小" placement="top-start">
                                    <div class="set-class">
                                        字体大小
                                    </div>
                                </el-tooltip>
                            </el-col>
                            <el-col :span="18">
                                <el-input-number v-model="fontsize" controls-position="right" @change="handleChange"
                                                 :min="10" :max="22"></el-input-number>
                            </el-col>
                        </el-row>
                        <el-row class="set-class">
                            <el-col :span="6">
                                <el-tooltip class="item" effect="dark" content="设置网页发送心跳的速率(X秒一次)"
                                            placement="top-start">
                                    <div class="set-class">
                                        心跳频率
                                    </div>
                                </el-tooltip>
                            </el-col>
                            <el-col :span="18">
                                <el-input-number v-model="hearttime" controls-position="right" @change="handleChange"
                                                 :min="30" :max="300"></el-input-number>
                            </el-col>
                        </el-row>
                        <el-button type="primary" round @click="saveSetting">保存设置</el-button>
                    </el-tab-pane>
                    <el-tab-pane>
                        <span slot="label"><i class="el-icon-date"></i>指令</span>
                        <el-row class="func-class" v-for="(item,index) in funcarray" :key="index">
                            <el-col :span="16">
                                <div style="width: 100%">
                                    <el-button @click="sendFunc(index)" style="width: 90%">{{ item.name }}</el-button>
                                </div>
                            </el-col>
                            <el-col :span="10">
                                <div class="button-class">
                                    <el-button type="success" icon="el-icon-edit" circle
                                               @click="editFunc(index)"></el-button>

                                    <el-button @click="removeFunc(index)" type="danger" icon="el-icon-delete"
                                               circle></el-button>
                                </div>
                            </el-col>
                        </el-row>
                        <el-button type="primary" round @click="addFunc">添加</el-button>
                    </el-tab-pane>
                </el-tabs>
            </el-col>
            <el-col :span="20">
                <TerminalTabs ref="terminalTabs"></TerminalTabs>
            </el-col>
        </el-row>
        <!--        -->
        <el-dialog title="指令设置" :visible.sync="dialogFormVisible">
            <el-form :model="form">
                <el-form-item label="指令名称" :label-width="formLabelWidth">
                    <el-input v-model="form.name" autocomplete="off"></el-input>
                </el-form-item>
                <el-form-item label="指令内容" :label-width="formLabelWidth">
                    <el-input v-model="form.content" autocomplete="off"></el-input>
                </el-form-item>
            </el-form>
            <div slot="footer" class="dialog-footer">
                <el-button @click="dialogFormVisible = false">取 消</el-button>
                <el-button type="primary" @click="updateOrAdd">确 定</el-button>
            </div>
        </el-dialog>
    </div>
</template>

<script>
import TerminalTabs from '../components/TerminalTabs';   // 引入子组件XTerm
import Tree from '../components/Tree';
import {uuid} from "@/util/util";


// 引入子组件XTerm

export default {
    name: "Terminal",
    components: {
        TerminalTabs: TerminalTabs,
        Tree: Tree,
    },
    data() {
        return ({
            opStatus: 'add',
            currIndex: -1,
            fontsize: 16,
            hearttime: 30,
            funcarray: [{
                "id": "sasasa8e98w",
                "name": '指令1',
                "content": 'ls ps'
            }],
            dialogFormVisible: false,
            form: {
                id: '',
                name: '',
                content: ''
            },
            formLabelWidth: '120px'
        })
    },
    methods: {
        addTab(node) {
            // const targetName = this.$refs.terminalTabs.editableTabsValue
            this.$refs.terminalTabs.addTab(node);
        },
        handleChange(e) {
            console.log(e)
            return e
        },
        saveSetting() {
            this.$api.post("/ecs/settheme", {
                "id": 1,
                "fontsize": this.fontsize,
                "hearttime": this.hearttime
            }, (res) => {
                console.log(res)
            })
        },
        sendFunc(index) {
            this.$copyText(this.funcarray[index].content).then( () => {
                this.$notify({
                    title: '成功',
                    message: '内容放置在粘贴板成功',
                    type: 'success'
                });
            }, () => {
                this.$notify({
                    title: '成功',
                    message: '内容放置在粘贴板成功',
                    type: 'success'
                });
            })
        },
        addFunc() {
            this.form = {}
            this.opStatus = 'add'
            this.dialogFormVisible = true
            return
        },
        editFunc(index) {
            this.opStatus = 'edit'
            this.currIndex = index
            this.dialogFormVisible = true
            this.form.name = this.funcarray[index].name
            this.form.content = this.funcarray[index].content
            this.form.id = this.funcarray[index].id

        },
        // 修改或者增加
        updateOrAdd() {
            // 更新
            if (this.opStatus === 'edit') {
                this.$api.post("command/update_command", {
                    id: this.form.id,
                    name: this.form.name,
                    content: this.form.content
                }, (res) => {
                    //this.funcarray.push(res.data)
                    if (res.status === 200) {
                        this.dialogFormVisible = false
                        this.funcarray.splice(this.currIndex, 1, {
                            id: this.form.id,
                            name: this.form.name,
                            content: this.form.content
                        })
                    }
                })
            }
            // 增加
            if (this.opStatus === 'add') {
                let id = uuid()
                this.$api.post("command/add_command", {
                    id: id,
                    name: this.form.name,
                    content: this.form.content
                }, () => {
                    this.funcarray.push({
                        id: id,
                        name: this.form.name,
                        content: this.form.content
                    })
                })
                this.dialogFormVisible = false
                this.opStatus = ''
            }
        },

        removeFunc(index) {
            this.$confirm('删除该指令, 是否继续?', '提示', {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning'
            }).then(() => {
                this.$api.get("command/remove_command", {id: index}, (res) => {
                    if (res.status === 200) {
                        this.funcarray.splice(index, 1)
                        this.$message({
                            type: 'success',
                            message: '删除成功!'
                        });
                    }
                })
            }).catch(() => {
                this.$message({
                    type: 'info',
                    message: '已取消删除'
                });
            });
        }
    },
    mounted: function () {
        // 获取数据库的配置
        let _this = this;
        // 获取主题
        this.$store.dispatch('getTheme', (data) => {
            console.log("theme", data)
            _this.fontsize = data.fontsize;
            _this.hearttime = data.hearttime;
        })
        // 获取指令
        this.$api.get("command/get_command", {}, (res) => {
            console.log(res)
            if (res.status == '200') {
                this.funcarray = res.data;
            }
        })
    }
}
</script>

<style scoped>

.set-class {
    display: flex;
    font-size: 12px;
    height: 100%;
    justify-content: center;
    align-items: center;
    margin-bottom: 10px;
}

.func-class {
    display: flex;
    flex-direction: row;
    align-items: center;
    margin: 10px 0 10px 0;

}

.button-class {
    display: flex;
    font-size: 16px;
    justify-content: space-around;
    align-items: center;
}
</style>