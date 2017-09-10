package simelectricity.api.node;

/**
 * Created by manageryzy on 9/8/2017.
 * Exception of no Complement found
 */
public class NoComplementException extends RuntimeException {
    private static final long serialVersionUID = 3461448718403576447L;

    public NoComplementException() {
        super("Mo Complement was found");
    }
}
