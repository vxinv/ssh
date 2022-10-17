<template>
    <div :id="divId"></div>
</template>

<script>
import "xterm/css/xterm.css";
import "xterm/lib/xterm.js";
import {Terminal} from "xterm";
import {FitAddon} from "xterm-addon-fit";


export default {
    name: 'BaseTerm',
    props: {
        divId: String,
        node: Object,
    },
    data() {
        return {
            term: {},
            socket: {},
            uuid_str: '',
            timeout: 10 * 1000, //58秒一次心跳
            timeoutObj: null, //心跳心跳倒计时
            serverTimeoutObj: null, //心跳倒计时
            timeoutnum: null, //断开 重连倒计时,
        };
    },
    methods: {
        initXterm() {
            let theme = this.$store.state.theme;
            //console.log("vuex-fontsize",theme.fontsize)
            this.term = new Terminal({
                rendererType: "canvas", //渲染类型
                cols: 170,//列数
                rows: 38, //行数
                convertEol: true, //启用时，光标将设置为下一行的开头
                scrollback: 100, //终端中的回滚量
                disableStdin: false, //是否应禁用输入
                cursorStyle: "underline", //光标样式
                cursorBlink: true, //光标闪烁
                fontWeight: 600,
                fontSize: theme.fontsize,
                fontFamily: " Consolas, Microsoft YaHei, 'Courier New', monospace ",
                theme: {
                    foreground: "#f7f0f0", //默认前景色
                    background: "#060101", //背景色
                    cursor: "#ffffff" //设置光标
                }
            });
            this.term.open(document.getElementById(this.divId));
            const fitAddon = new FitAddon();
            this.term.loadAddon(fitAddon);
            // 支持输入与粘贴方法
            let _this = this; //一定要重新定义一个this，不然this指向会出问题
            this.term.onData(function (key) {
                // let order = ["stdin",key]; //这里key值是你输入的值，数据格式一定要找后端要！！！！
                _this.socket.onsend(JSON.stringify({type: "command", data: key})); //转换为字符串
            });
        },
        init(url) {
            // 实例化socket
            this.socket = new WebSocket(url);
            // 监听socket连接
            this.socket.onopen = this.open;
            // 监听socket错误信息
            this.socket.onerror = this.error;
            // 监听socket消息
            this.socket.onmessage = this.getMessage;

            // 发送socket消息
            this.socket.onsend = this.send;
        },
        open: function () {
            //console.log("socket连接成功");
            // 初始化 终端
            this.initXterm();
            this.socket.onsend(JSON.stringify({type: "connect", data: this.node.id})); //转换为字符串
            this.$emit('changeLoading')
            this.$notify({
                title: '连接成功',
                message: '连接shell成功',
                type: 'success'
            });
            // 开始心跳
            this.heartbeat()
        },
        error: function () {
           // console.log("连接错误");
        },
        close: function () {
            this.socket.close();
            this.$notify({
                title: '断开成功',
                message: '断开shell成功',
                type: 'success'
            });
            //console.log("socket已经关闭");
        },
        getMessage: function (msg) {
            //console.log(msg);
            if (msg.data != '\u0007'){
                this.term.write(msg.data);
            }
        },
        send: function (order) {
            this.socket.send(order);
        },

        /* self.serverTimeoutObj = setTimeout(function () {
                     //超时关闭
                     self.ws.close();
                 }, self.timeout);*/

        heartbeat: function () {
            // 开启心跳
            var self = this;
            self.timeoutObj && clearInterval(self.timeoutObj);
            //self.serverTimeoutObj && clearTimeout(self.serverTimeoutObj);
            self.timeoutObj = setInterval( () => {
                //这里发送一个心跳，后端收到后，返回一个心跳消息，
                if (self.socket.readyState === 1) {
                    ///console.log("发送心跳")
                    //如果连接正常
                    self.socket.send(JSON.stringify({type: "heartbeat", data: ''}));
                }
            }, self.timeout);
        }
    },
    mounted() {
        let url = "ws://127.0.0.1:6543/terminal";
        // console.log("url", url)
        this.init(url);
    },
    destroyed() {
        //console.log("页面destroyed")
        clearInterval(this.timeoutObj)
        this.socket.close(); //离开路由之后断开websocket连接
    },
};
</script>

<style scoped>

</style>