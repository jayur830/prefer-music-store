<%@ page import="org.springframework.security.core.Authentication" %>
<%@ page import="org.springframework.security.core.context.SecurityContextHolder" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html; charset=utf-8" pageEncoding="utf-8" %>
<%@ include file="/WEB-INF/include/header.jsp" %>
<sec:authorize access="!hasRole('ROLE_ADMIN')">
    <c:redirect url="/login" />
</sec:authorize>
<%
    String username = "";
    boolean authentication = false;
    boolean isAdmin = false;
    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
    if (auth.getPrincipal() != null) {
        username = auth.getName();
        authentication = true;
        isAdmin = auth.getAuthorities().toArray()[0].toString().equals("ROLE_ADMIN");
    }
%>
<html>
<head>
    <title><%=username%> 관리자님 환영합니다.</title>
    <link rel="stylesheet" href="<c:url value="/resources/css/center_form.css" />" />
    <style>
        #form {
            width: 100%;
            height: 100%;
        }

        input[type=button] {
            font-size: 10pt;
            padding: 5px 20px;
            margin: 5px 8px;
        }

        input[type=text] {
            font-size: 12pt;
            margin: 10px;
        }

        #btns {
            height: 65px;
            text-align: right;
        }

        #search_area {
            height: 41.5px;
            text-align: left;
        }

        #list_area {
            border-radius: 10px;
            height: 100%;
            overflow: scroll;
            padding: 20px;
        }

        #search {
            width: 300px;
        }

        #list {
            table-layout: fixed;
            width: 100%;
        }

        .play_btn {
            text-align: center;
            width: 70px;
            height: 50px;
        }

        .song_desc {
            font-size: 18pt;
        }

        .range {
            width: 300px;
            text-align: center;
        }

        .element {
            padding: 10px 0;
            transition: background-color 0.2s, opacity 0.2s;
        }

        .element:hover {
            background-color: #f5f5f5;
            opacity: 0.8;
        }

        .text_over {
            overflow: hidden;
            text-overflow: ellipsis;
            white-space: nowrap;
        }

        @media screen and (max-width: 786px) {
            input[type=button] {
                font-size: 14pt;
                padding: 10px 30px;
                margin: 10px 10px;
            }

            input[type=text] {
                font-size: 16pt;
            }

            #btns {
                height: 39px;
            }

            #search_area {
                height: 38.5px;
            }

            #list_area {
                padding: 10px;
            }

            .play_btn {
                height: 50px;
            }

            .song_desc {
                font-size: 16pt;
            }
        }
    </style>
</head>
<body>
    <table id="form">
        <tr>
            <td style="text-align: left;">
                <input type="button" id="exec" value="" class="btn_light_dark" />
            </td>
            <td id="btns">
                <input type="button" id="user_info" value="회원정보 수정" class="btn_light_dark" />
                <input type="button" id="logout" value="로그아웃" class="btn_dark_light" />
            </td>
        </tr>
        <tr>
            <td id="search_area" colspan="2">
                <input type="text" id="search" placeholder="가수 이름 또는 곡 제목 입력" />
            </td>
        </tr>
        <tr><td colspan="2"><div id="list_area"><table id="list"></table></div></td></tr>
    </table>
    <script src="<c:url value="/resources/js/date_format.js"/>"></script>
    <script src="<c:url value="/resources/js/user.js"/>"></script>
    <script src="<c:url value="/resources/js/playlist.js"/>"></script>
    <script>
        $.get("/is_started", { username: "<%=username%>" })
            .done(started => $("#exec").val("서버 " + (started ? "중지" : "시작")))
            .fail(e => {
                console.log(e);
                alert("Failed to connect to server.");
            });

        $("#exec").on("click", () => {
            $("#exec").attr("disabled", true);
            $.get("/exec", { username: "<%=username%>" })
                .done(data => {
                    if (data.serverStarted) {
                        alert("서버가 실행되었습니다.");
                        $("#exec").val("서버 중지");
                        $("#list").html(playlistToString(null, <%=authentication%>, <%=isAdmin%>, data.playlist));
                    } else {
                        alert("서버가 중지되었습니다.");
                        $("#exec").val("서버 시작");
                        $("#list").html("");
                    }
                    $("#exec").attr("disabled", false);
                }).fail(e => {
                    console.log(e);
                    alert("Failed to connect to server.");
                });
        });

        $("#login").on("click", () => location.replace("/login"));
        $("#logout").on("click", () => {
            if (confirm("로그아웃 하시겠습니까?"))
                user.logout("${_csrf.parameterName}", "${_csrf.token}");
        });
        $("#user_info").on("click", () => location.replace("/user_info"));

        $(document).ready(() => getCurrentPlaylist(null, <%=authentication%>, <%=isAdmin%>));

        $("#search").on("keyup", () => {
            const keyword = $("#search").val();
            if (keyword === "") getCurrentPlaylist(null, <%=authentication%>, <%=isAdmin%>);
            else $.get("search_action", {
                    username: "<%=username%>" === "" ? null : "<%=username%>",
                    keyword: keyword
                }).done(playlist => {
                    if (playlist != null) $("#list").html(playlistToString(null, <%=authentication%>, <%=isAdmin%>, playlist));
                }).fail(e => {
                    console.log(e);
                    alert("Failed to connect to server.");
                });
        });
    </script>
</body>
</html>