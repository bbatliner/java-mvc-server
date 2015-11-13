package bbatliner.http;

public class HttpHeader {

	private String name;
	private String value;
	
	public HttpHeader(String raw) {
		String[] vals = raw.split(":", 2);
		this.name = vals[0];
		this.value = vals[1].trim();
	}
	
	public HttpHeader(String name, String value) {
		this.name = name;
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
	@Override
	public String toString() {
		return name + ": " + value;
	}

}
