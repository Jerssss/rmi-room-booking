package client.admin.model;

import client.ClientMain;
import shared.Terminal;
import shared.interfaces.admin.AdminProcessors;

import java.rmi.RemoteException;
import java.util.List;

public class ModifyTerminalStatusModel {
    private AdminProcessors adminProcessors;

    public ModifyTerminalStatusModel() {
        this.adminProcessors = ClientMain.getAdminProcessors(); // Get RMI instance
    }

    public List<Terminal> fetchReservations() {
        System.out.println("[CLIENT] fetchReservations() method called.");

        try {
            if (adminProcessors == null) {
                System.err.println("[ERROR] AdminProcessors RMI service is NULL!");
                return null;
            }

            List<Terminal> terminals = adminProcessors.getAllTerminals();

            if (terminals == null || terminals.isEmpty()) {
                System.out.println("[SERVER] No reservations found via RMI.");
            } else {
                System.out.println("[CLIENT] Loaded " + terminals.size() + " reservations via RMI.");
                for (Terminal terminal : terminals) {
                    System.out.println("[SERVER] " + terminal);
                }
            }

            return terminals;
        } catch (RemoteException e) {
            System.err.println("[ERROR] RMI call failed: " + e.getMessage());
            return null;
        }
    }
}
