$(document).ready( function () {
    //必须随着页面加载先执行，不然有缓存界面会显示bug
    //未登录状态下改变最上面的管理行
    if (sessionStorage.getItem("user") == null){
        //设置显示登录
        $("#loginStatus").empty().html("&nbsp;&nbsp;" + "登录")
        // //移除管理模块的class样式并清空元素
        // $("#menuCaret").removeClass("caret")
        // $("#uiMenu").removeClass("dropdown-menu top-dropdown-ul").empty()
    }else{ //不为空则已经登录
        $.ajax({
            url : "/user/queryUser" ,
            type: "get",
            dataType: "json",
            success: function (res) {
                if(res.data.first){
                    jQuery(document).ready(function($) {
                        $('#myModal').modal().options
                    })
                }
                //用户登录了则改变最上面的导航条
                changeMenu(res)
            }
        });
    }
});

//登录状态下改变最上面的管理行
function changeMenu(res) {
    let user = res.data;
    //不为空代表已经登录
    if (user.username != null){
        //修改为登录的用户名
        $("#loginStatus").html("&nbsp;" + user.username)
        //添加退出按钮
        let exitStr = "<li>"+ "<span class=\"fa fa-sign-out\"></span>"
            + "<a style= \"color: #1a1d1d\" href=\"javascript:void(0);\" onclick=\"exitLogin()\">&nbsp;注销</a>"
            + "</li>"
        $("#topMenu").append(exitStr)

        //移除跳转属性
        document.getElementById("loginStatus").removeAttribute("href")
    }
}

//退出功能
function exitLogin(){
    if (sessionStorage.getItem("user") == null){
        swal({
            title: "警告",
            text: "尚未登录，请先登录！",
            type: "warning",
            showConfirmButton:true,
            confirmButtonText:'确认',
            // timer: 4000,
        });
        // alert("尚未登录，请先登录！")
    }else{
        $.ajax({
            url: "/user/exit",
            type: "get",
            dataType: "json",
            success:function (json) {
                if (json.state == 200){
                    swal({
                        title: "注销成功",
                        text: "即将为您自动跳转到首页...",
                        type: "success",
                        showConfirmButton:false,
                        timer: 4000,
                    });
                    sessionStorage.removeItem("user")
                    //动画过渡完跳转
                    setTimeout(function(){
                        window.location.href="/";
                        return false;
                    },1000);
                    // alert("注销成功!")
                    // sessionStorage.removeItem("user")
                    // location.href = "/"
                }
            },
            error:function () {
                swal({
                    title: "错误",
                    text: "服务器出现未知异常，退出登录失败！",
                    type: "error",
                    showConfirmButton:true,
                    confirmButtonText:'确认',
                    // timer: 4000,
                });
                // alert("服务器出现未知异常，退出登录失败")
            }
        })
    }
}