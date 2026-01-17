package splitwise.model.split;

import splitwise.model.User;

/**
 * Represents an equal split where each participant pays the same amount.
 * The actual amount is calculated based on total expense / number of participants.
 */
public class EqualSplit extends Split {

    public EqualSplit(User user) {
        super(user);
    }

    @Override
    public boolean validate() {
        // Equal splits are always valid as amount is calculated automatically
        return true;
    }
}
