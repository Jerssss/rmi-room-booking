package shared.callback;
import shared.Log;
import shared.Reservation;
import shared.Terminal;
import java.util.List;

public interface UpdateTable {
    /**
     * Update the table or data structure for terminals.
     *
     * @param terminals the updated list of terminals.
     */
    void updateTerminals(List<Terminal> terminals);

    /**
     * Update the table or data structure for reservations.
     *
     * @param reservations the updated list of reservations.
     */
    void updateReservations(List<Reservation> reservations);

    /**
     * Update the table or data structure for logs.
     *
     * @param logs the updated list of logs.
     */
    void updateLogs(List<Log> logs);
}
