package com.prefer_music_store.app.util;

import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

// RFM 기반 사용자 점수 부여
@Component("rfmCalculator")
public class UserRFMCalculator {
	private final int SECOND = 1000, MINUTE = SECOND * 60, HOUR = MINUTE * 60,
			DATE = HOUR * 24, MONTH = DATE * 30, YEAR = DATE * 365;

	@Resource(name = "dateFormat")
	private DateFormat dateFormat;

	public int[] getRFM(String recentRatingDateTime, long ratingCount, double avgActiveTime) {
		// 최근 평점 반영 시간이 null 이라면 Long 자료형 범위의 최댓값을 할당하고, 그렇지 않을 경우 최근 평점 반영 시간과 현재 시간과의 차이를 구한다.
		// null 일 경우 Long 자료형 범위의 최댓값을 할당하는 이유는 R을 구할 시 인수값을 365 이상으로 하여 0점으로 반환하게 하기 위함이다.
		long ratingTimeDiff = recentRatingDateTime == null || recentRatingDateTime.equals("null") ?
				Long.MAX_VALUE : calculateTimeDiff(recentRatingDateTime, this.dateFormat.format(new Date()), this.DATE);
		// R, F, M을 구한다.
		int r = getRecencyScore(ratingTimeDiff), f = getFrequencyScore(ratingCount), m = getDurationScore(avgActiveTime);
		return new int[] { r, f, m };
	}

	private long calculateTimeDiff(String time1, String time2, int timeFormat) {
        long time = 0;
        try {
            Date date1 = this.dateFormat.parse(time1);
            Date date2 = this.dateFormat.parse(time2);
            time = Math.abs(date2.getTime() - date1.getTime()) / timeFormat;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return time;
    }
	
	private int getRecencyScore(long date) {
		if (date < 3) return 8;
		else if (date < 7) return 7;
		else if (date < 14) return 6;
		else if (date < 30) return 5;
		else if (date < 60) return 4;
		else if (date < 100) return 3;
		else if (date < 200) return 2;
		else if (date < 365) return 1;
		else return 0;
	}
	
	private int getFrequencyScore(long count) {
		if (count == 0) return 0;
		else if (count == 1) return 1;
		else if (count >= 2 && count < 5) return 2;
		else if (count >= 5 && count < 10) return 3;
		else if (count >= 10 && count < 15) return 4;
		else if (count >= 15 && count < 20) return 5;
		else if (count >= 20 && count < 25) return 6;
		else if (count >= 25 && count < 30) return 7;
		else if (count >= 30 && count < 40) return 8;
		else if (count >= 40 && count < 50) return 9;
		else return 10;
	}
	
	private int getDurationScore(double avgMinute) {
		if (avgMinute == 0) return 0;
		else if (avgMinute > 0 && avgMinute < 5) return 1;
		else if (avgMinute >= 5 && avgMinute < 10) return 2;
		else if (avgMinute >= 10 && avgMinute < 15) return 3;
		else if (avgMinute >= 15 && avgMinute < 20) return 4;
		else if (avgMinute >= 20 && avgMinute < 30) return 5;
		else if (avgMinute >= 30 && avgMinute < 40) return 6;
		else if (avgMinute >= 40 && avgMinute < 50) return 7;
		else return 8;
	}
}
