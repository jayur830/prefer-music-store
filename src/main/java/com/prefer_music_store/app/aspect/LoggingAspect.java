package com.prefer_music_store.app.aspect;

import com.prefer_music_store.app.repo.PlaylistDAO;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

@Aspect
@Component
public class LoggingAspect {
    @Resource(name = "playlistDAO")
    private PlaylistDAO playlistDAO;
    @Resource(name = "rfmScores")
    private Map<Integer, Integer> rfmScores;
    @Resource(name = "userIndexToId")
    private List<String> userIndexToId;
    @Resource(name = "itemIndexToId")
    private List<Integer> itemIndexToId;

    @AfterReturning(value = "execution(* *..UserHistoryPlaylistRecommendAlgorithm.predict())", returning = "list")
    public void printUserHistoryPlaylist(List<Object> list) {
        if (list == null || list.isEmpty())
            System.out.println("\nThere is no current user history playlist");
        else {
            System.out.println("\n[User History Playlist]");
            for (Object element : list) {
                Map<String, Object> item = this.playlistDAO.getArtistAndSongName((int) element);
                System.out.println(element + ": " + item);
            }
        }
    }

    @AfterReturning(value = "execution(* *..AgeGenderPlaylistRecommendAlgorithm.predict())", returning = "list")
    public void printAgeGenderPlaylist(List<Object> list) {
        if (list == null || list.isEmpty())
            System.out.println("\nThere is no current age/gender playlist");
        else {
            System.out.println("\n[Age/Gender Playlist]");
            for (Object element : list) {
                Map<String, Object> item = this.playlistDAO.getArtistAndSongName((int) element);
                System.out.println(element + ": " + item);
            }
        }
    }

    @AfterReturning(value = "execution(* *..api.Recommender.recommend(..))", returning = "list")
    public void printTotalPlaylist(List<Object> list) {
        if (list == null || list.isEmpty())
            System.out.println("\nThere is no current playlist");
        else {
            System.out.println("\n[Total Playlist]");
            for (Object element : list) {
                Map<String, Object> item = this.playlistDAO.getArtistAndSongName((int) element);
                System.out.println(element + ": " + item);
            }
        }
    }

    @After("execution(* *..UserHistoryPlaylistRecommendAlgorithm.initFeatures())")
    public void printRFMScores() {
        System.out.println("\n[RFM Scores]");
        for (Map.Entry<Integer, Integer> iter : this.rfmScores.entrySet())
            System.out.println(this.userIndexToId.get(iter.getKey()) + " : " + iter.getValue());
    }

    @AfterReturning(value = "execution(* *..UserRatingDAOImpl.getAvgRatingsGroupBy*())", returning = "list")
    public void printRatings(List<Map<String, Object>> list) {
        System.out.println("\n[Ratings]");
        for (Map<String, Object> rating : list)
            System.out.println(rating);
    }

    @AfterReturning(value = "execution(* *..AgeGenderPlaylistRecommendAlgorithm.norm(..))", returning = "item")
    public void printAfterReturningAgeGenderNorm(Map<String, Object> item) {
        System.out.println("[Normalization]");
        for (Map.Entry<String, Object> i : item.entrySet())
            System.out.println(String.format("%s: %.4f", i.getKey(), (double) i.getValue()));
    }

    @Around(value = "execution(* *..EmailUtils.*(..))")
    public Object printEmailLog(ProceedingJoinPoint joinPoint) {
        Object point = null;
        try {
            System.out.println("Send email...");
            point = joinPoint.proceed();
            System.out.println("Success to send email!");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        return point;
    }
}
