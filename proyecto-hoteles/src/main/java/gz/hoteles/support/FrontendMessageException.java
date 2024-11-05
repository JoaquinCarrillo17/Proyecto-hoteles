package gz.hoteles.support;

import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
public class FrontendMessageException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private HttpStatus status = HttpStatus.BAD_REQUEST;

	public FrontendMessageException(String msg) {
		super(msg);
	}
	
	public FrontendMessageException(String msg, HttpStatus status) {
		super(msg);
		this.status = status;
	}	
}
