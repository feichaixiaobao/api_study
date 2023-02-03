package jp.feichaixiaobao.api.user.LeoJapan.entity;

public class MovieInfoEntity {

	private String title;	
	private String originalTitle;
	private boolean isTv;
	private String year;
	private String id;
	private String poster;
	private MovieRatingEntity rating;
	private String roleDesc;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getOriginalTitle() {
		return originalTitle;
	}
	public void setOriginalTitle(String originalTitle) {
		this.originalTitle = originalTitle;
	}
	public boolean isTv() {
		return isTv;
	}
	public void setTv(boolean isTv) {
		this.isTv = isTv;
	}
	public String getYear() {
		return year;
	}
	public void setYear(String year) {
		this.year = year;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getPoster() {
		return poster;
	}
	public void setPoster(String poster) {
		this.poster = poster;
	}
	public MovieRatingEntity getRating() {
		return rating;
	}
	public void setRating(MovieRatingEntity rating) {
		this.rating = rating;
	}
	public String getRoleDesc() {
		return roleDesc;
	}
	public void setRoleDesc(String roleDesc) {
		this.roleDesc = roleDesc;
	}
	
}
