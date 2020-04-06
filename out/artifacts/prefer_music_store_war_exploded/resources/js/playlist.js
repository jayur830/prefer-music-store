let audio = null;
let isPlaying = false, play = id => {
    if (audio == null) {
        audio = $("#m" + id)[0];
        audio.play();
        isPlaying = true;
    } else if (isPlaying) {
        if (audio.id !== id)
            audio = $("#m" + id)[0];
        audio.pause();
        isPlaying = false;
    } else if (!isPlaying) {
        if (audio.id !== id)
            audio = $("#m" + id)[0];
        audio.play();
        isPlaying = true;
    }
};

const playlistToString = (userId, auth, playlist) => {
    let htmlStr = "";
    for (let i = 0; i < playlist.length; ++i) {
        const songId = playlist[i].song_id,
            artist = playlist[i].artist,
            songName = playlist[i].song_name,
            rating = playlist[i].rating;
        htmlStr += "<tr class=\"element\">";
        htmlStr += "<td class=\"element play_btn\">";
        htmlStr += `<div id=\"b${songId}\" class=\"fas fa-play fa-2x\" style=\"cursor: pointer;\" onclick=\"play('${songId}');\">`;
        htmlStr += `<audio id=\"m${songId}\">`;
        htmlStr += `<source src=\"resources/audio/${songId}.mp3\" />`;
        htmlStr += "</audio>";
        htmlStr += "</div></td>";
        htmlStr += "<td class=\"element song_desc\">";
        htmlStr += `<div class=\"text_over\" style=\"font-size: 100%; font-weight: bold;\">${songName}</div>`;
        htmlStr += `<div class=\"text_over\" style=\"font-size: 70%;\">${artist}</div>`;
        htmlStr += "</td>";
        if (auth) {
            htmlStr += `<td id=\"r${songId}\" class=\"element range\">`;
            for (let j = 0; j < 5; ++j)
                htmlStr += `<span class=\"fa${rating === 0 ? "l" : (j < rating ? "s" : "l")} fa-star fa-2x rating\" 
                                        onclick=\"playlistUpdate('${userId}', '${songId}', ${parseFloat(j + 1)});\"></span>`;
            htmlStr += "</td>";
        }
        htmlStr += "</tr>";
    }
    return htmlStr;
};

const getCurrentPlaylist = (username, authentication) =>
    $.get("playlist_action", { username: username === "" ? null : username })
        .done(playlist => {
            if (playlist != null) $("#list").html(playlistToString(username, authentication, playlist));
        }).fail(e => {
            console.log(e);
            alert("Failed to connect to server.");
        });

const playlistUpdate = (userId, itemId, rating) => {
    if (confirm("평점을 반영하시겠습니까?"))
        $.get("rating_action", {
            user_id: userId,
            item_id: itemId,
            rating: rating,
            rating_datetime: new Date().format("yyyy-MM-dd HH:mm:ss")
        }).done(() => {
            let star = $("#r" + itemId).find(".fa-star");
            star.removeClass("fas");
            star.addClass("fal");
            for (let i = 0; i < parseInt(rating); ++i)
                star.eq(i).addClass("fas");
            alert("평점이 반영되었습니다.");
        }).fail(e => {
            console.log(e);
            alert("Failed to connect to server.");
        });
};