package common;

public enum HttpMethod {
	GET, POST;

	public boolean isPost() {
		return this == HttpMethod.POST;
	}
}
