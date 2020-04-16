class User {
    checkUsername(csrfName, csrfValue, username) {
        $.post("/check_username", { [csrfName]: csrfValue, username })
            .done(responseData => {
                if ($.parseJSON(responseData).valid) {
                    alert("사용할 수 있는 아이디입니다.");
                } else alert("이미 존재하는 아이디입니다.");
            }).fail(e => {
                console.log(e);
                alert("Failed to connect to server.");
            });
    }

    signUp(csrfName, csrfValue, username, password,
           name, birth, email, gender, age) {
        $.post("/sign_up_action", {
            [csrfName]: csrfValue,
            username, password, name, birth, email, gender, age
        }).done(() => {
            /**
             * @TODO Implement the logic
             * */
            alert("회원가입이 완료되었습니다.");
            location.replace("/");
        }).fail(e => {
            console.log(e);
            alert("Failed to connect to server.");
        });
    }

    login(csrfName, csrfValue, username, password) {
        $.post("/login_action", {
            [csrfName]: csrfValue,
            username, password
        }).done(responseData => {
            const data = $.parseJSON(responseData);
            /**
             * @TODO Implement the logic
             * */
            if (data.username != null && data.targetUrl != null) {
                alert(data.username + "님 환영합니다.");
                location.replace(data.targetUrl);
            } else if (data.error != null) $("#login_error").html(data.error);
        }).fail(e => {
            console.log(e);
            alert("Failed to connect to server.");
        });
    }

    logout(csrfName, csrfValue) {
        $.post("/logout", { [csrfName]: csrfValue })
            .done(() => {
                /**
                 * @TODO Implement the logic
                 * */
                alert("로그아웃 되었습니다. 안녕히 가세요.");
                location.replace("/login");
            }).fail(e => {
                console.log(e);
                alert("Failed to connect to server.");
            });
    }

    findUsername(csrfName, csrfValue, name, birth, email) {
        $.post("/find_username_action", {
            [csrfName]: csrfValue,
            name, birth, email
        }).done(responseData => {
            const username = $.parseJSON(responseData).username;
            let htmlStr = `<table id="child_form">`;
            if (username != null)
                htmlStr +=
                    `<tr>
                        <td>
                            <h3>회원님의 아이디는 ${username}입니다.</h3>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center;">
                            <input type="button" value="비밀번호 찾기" class="btn_light_dark" onclick="location.replace('/find_password');" />
                            <input type="button" value="돌아가기" class="btn_light_dark" onclick="location.replace('/login');" />
                        </td>
                    </tr>`;
            else
                htmlStr +=
                    `<tr>
                        <td>
                            <h4>입력하신 정보와 일치하는 계정이 존재하지 않습니다.</h4>
                        </td>
                    </tr>
                    <tr>
                        <td style="text-align: center;">
                            <input type="button" value="돌아가기" class="btn_light_dark" onclick="location.replace('/login');" />
                        </td>
                    </tr>`;
            htmlStr += "</table>";

            $("#child_form").html(htmlStr);
            $("input[type=button]").css({ fontSize: "12pt", margin: "0 10px" });
        }).fail(e => {
            console.log(e);
            alert("Failed to connect to server.");
        });
    }

    findPassword(csrfName, csrfValue, username, email) {
        $.post("/find_password_action", {
            [csrfName]: csrfValue,
            username, email
        }).done(responseData => {
            const valid = $.parseJSON(responseData).valid;
            let htmlStr = "<table id=\"child_form\">";
            if (valid) {
                htmlStr += "<tr><td style=\"text-align: center\"><h3>";
                htmlStr += `회원님의 임시 비밀번호를 ${email}로 보내드렸습니다.<br />`;
                htmlStr += "확인 후 변경 바랍니다.";
                htmlStr += "</h3></td></tr>";
            } else htmlStr += `<tr><td><h2>입력하신 정보와 일치하는 계정이 존재하지 않습니다.</h2></td></tr>`;
            htmlStr += "<tr>";
            htmlStr += "<td style=\"text-align: center;\"><input type=\"button\" value=\"돌아가기\" class=\"btn_light_dark\" onclick=\"location.replace('/login');\" /></td>";
            htmlStr += "</tr>";
            htmlStr += "</table>";

            $("#child_form").html(htmlStr);
            $("input[type=button]").css({ fontSize: "12pt", margin: "0 10px" });
        }).fail(e => {
            console.log(e);
            alert("Failed to connect to server.");
        });
    }

    initUserInfo(csrfName, csrfValue, username) {
        $.post("/get_user_info", { [csrfName]: csrfValue, username })
            .done(responseData => {
                this.userInfo = responseData;
                $("#username").val(this.userInfo.username);
                $("#password").val(this.userInfo.password);
                $("#name").val(this.userInfo.name);
                $("#birth").val(this.userInfo.birth);
                $("#email").val(this.userInfo.email);

                const genderString = ["female", "male"], genderIndex = this.userInfo.gender;
                $("#label_" + genderString[genderIndex]).removeClass("fal");
                $("#label_" + genderString[genderIndex]).addClass("fas");
                $("#gender_" + (this.userInfo.gender === 0 ? "fe" : "") + "male").attr("checked", true);
            }).fail(e => {
                console.log(e);
                alert("Failed to connect to server.");
            });
    }

    getUserInfo() {
        return this.userInfo;
    }

    edit(csrfName, csrfValue, username, password,
         name, birth, email, gender, age) {
        $.post("/edit_user_info", {
            [csrfName]: csrfValue,
            username, password, name, birth, email, gender, age
        }).done(() => {
            alert("회원정보 수정이 완료되었습니다.");
            location.replace("/main");
        }).fail(e => {
            console.log(e);
            alert("Failed to connect to server.");
        });
    }

    deleteUser(csrfName, csrfValue, username, password) {
        $.post("/is_valid", { [csrfName]: csrfValue, username, password })
            .done(responseData => {
                const data = $.parseJSON(responseData);
                if (data.valid)
                    $.post("/delete_user_info", { [csrfName]: csrfValue, username })
                        .done(() => {
                            alert("그 동안 이용해주셔서 감사합니다. 안녕히 가세요.");
                            this.logout(csrfName, csrfValue);
                        }).fail(e => {
                            console.log(e);
                            alert("Failed to connect to server.");
                        });
                else $("#auth_failed").html(data.error);
            }).fail(e => {
                console.log(e);
                alert("Failed to connect to server.");
            });
    }
}

let user = new User();