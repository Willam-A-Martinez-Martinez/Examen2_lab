package examen2_lab;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PSNUsers {
    private RandomAccessFile gestPsn;
    public HashTable users;
    private final String ARCHIVO_PSN = "psn.psw";

    public PSNUsers() throws FileNotFoundException, IOException {
        gestPsn = new RandomAccessFile(ARCHIVO_PSN, "rw");
        users = new HashTable();
        reloadHashTable();
    }

    private void reloadHashTable() throws IOException {
        users = new HashTable();
        gestPsn.seek(0);
        while (gestPsn.getFilePointer() < gestPsn.length()) {
            long pos = gestPsn.getFilePointer();
            String username = gestPsn.readUTF();
            int points = gestPsn.readInt();
            int trophy = gestPsn.readInt();
            boolean active = gestPsn.readBoolean();
            if (active) {
                users.add(username, pos);
            }
        }
    }

    public boolean addUser(String user) throws IOException {
        if (user == null || user.trim().isEmpty()) {
            throw new IllegalArgumentException("Username no puede ser nulo o vacío");
        }

        gestPsn.seek(0);
        while (gestPsn.getFilePointer() < gestPsn.length()) {
            long pos = gestPsn.getFilePointer();
            String username = gestPsn.readUTF();
            gestPsn.readInt();
            gestPsn.readInt();
            boolean active = gestPsn.readBoolean();
            
            if (username.equals(user)) {
                if (active) {
                    return false;
                }
                gestPsn.seek(pos);
                gestPsn.writeUTF(user);
                gestPsn.writeInt(0);
                gestPsn.writeInt(0);
                gestPsn.writeBoolean(true);
                users.add(user, pos);
                return true;
            }
        }

        long posicion = gestPsn.length();
        gestPsn.seek(posicion);
        gestPsn.writeUTF(user);
        gestPsn.writeInt(0);
        gestPsn.writeInt(0);
        gestPsn.writeBoolean(true);
        users.add(user, posicion);
        return true;
    }

    public void deactivateUser(String user) throws IOException {
        if (user == null) return;

        long pos = users.search(user);
        if (pos != -1) {
            gestPsn.seek(pos);
            gestPsn.readUTF();
            gestPsn.readInt();
            gestPsn.readInt();
            gestPsn.writeBoolean(false);
            users.remove(user);
        }
    }

    public void addTrophieTo(String username, String trophyGame, String trophyName, Trophy type) throws IOException {
        if (username == null || trophyGame == null || trophyName == null || type == null) {
            throw new IllegalArgumentException("Parámetros no pueden ser nulos");
        }

        long posicion = users.search(username);
        if (posicion == -1) {
            throw new IOException("Usuario no encontrado");
        }

        gestPsn.seek(posicion);
        String storedUser = gestPsn.readUTF();
        if (!storedUser.equals(username)) {
            throw new IOException("Inconsistencia en datos");
        }

        int points = gestPsn.readInt();
        int trofeos = gestPsn.readInt();
        boolean activo = gestPsn.readBoolean();

        if (!activo) {
            throw new IOException("Usuario inactivo");
        }

        points += type.getPoints();
        trofeos++;

        gestPsn.seek(posicion + username.length() + 2);
        gestPsn.writeInt(points);
        gestPsn.writeInt(trofeos);

        try (RandomAccessFile trophysF = new RandomAccessFile("Trofeos.psw", "rw")) {
            trophysF.seek(trophysF.length());
            trophysF.writeUTF(username);
            trophysF.writeUTF(type.name());
            trophysF.writeUTF(trophyGame);
            trophysF.writeUTF(trophyName);
            trophysF.writeUTF(new SimpleDateFormat("dd/MM/yyyy").format(new Date()));
        }
    }

    public String playerInfo(String username) throws IOException {
        if (username == null) {
            throw new IllegalArgumentException("Username no puede ser nulo");
        }

        long posicion = users.search(username);
        if (posicion == -1) {
            return "El usuario no fue encontrado";
        }

        StringBuilder playerInfo = new StringBuilder();
        gestPsn.seek(posicion);

        String nombre = gestPsn.readUTF();
        int points = gestPsn.readInt();
        int trofeos = gestPsn.readInt();
        boolean estaActivo = gestPsn.readBoolean();

        playerInfo.append("Usuario: ").append(nombre).append("\n")
                 .append("Puntos: ").append(points).append("\n")
                 .append("Activo: ").append(estaActivo ? "Sí" : "No").append("\n")
                 .append("Cantidad de trofeos: ").append(trofeos).append("\n\n")
                 .append("Trofeos:\n");

        try (RandomAccessFile trophyF = new RandomAccessFile("Trofeos.psw", "r")) {
            while (trophyF.getFilePointer() < trophyF.length()) {
                String user = trophyF.readUTF();
                if (user.equals(username)) {
                    String tipo = trophyF.readUTF();
                    String juego = trophyF.readUTF();
                    String descripcion = trophyF.readUTF();
                    String fecha = trophyF.readUTF();
                    playerInfo.append(fecha).append(" - ")
                             .append(tipo).append(" - ")
                             .append(juego).append(" - ")
                             .append(descripcion).append("\n");
                } else {
                    trophyF.readUTF();
                    trophyF.readUTF();
                    trophyF.readUTF();
                    trophyF.readUTF();
                }
            }
        } catch (FileNotFoundException e) {
            playerInfo.append("No se encontraron trofeos\n");
        }

        return playerInfo.toString();
    }
}