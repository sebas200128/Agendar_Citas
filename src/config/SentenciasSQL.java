package config;

public class SentenciasSQL {

    // USUARIOS
    public static final String LOGIN = "SELECT * FROM usuarios WHERE usuario = ? AND password = ?";

    // PERSONAS CRUD
    public static final String REGISTRAR = "INSERT INTO paciente (nombre,dni,genero,fecha_nacimiento) VALUES (?,?,?,?)";

    // LISTAR PARA TABLA 
    public static final String LISTAR = "SELECT * FROM paciente";

    // CITAS
    public static final String REGISTRAR_CITA = "INSERT INTO citas (id_paciente, fecha, hora) VALUES (?,?,?)";

    // VERIFICACIONES VENTANA CITAS
    public static final String VERIFICAR_CITA_PACIENTE = "SELECT * FROM citas WHERE id_paciente = ?";
    public static final String VERIFICAR_FECHA_OCUPADA = "SELECT * FROM citas WHERE fecha = ?";
    //FECHA + HORA
    public static final String VERIFICAR_HORARIO_OCUPADO = "SELECT * FROM citas WHERE fecha = ? AND hora = ?";

    //JOIN (Combina Citas y Pacientes)
    public static final String LISTAR_CITAS_DETALLE
            = "SELECT c.id, p.nombre, c.fecha, c.hora "
            + // Agregamos c.hora
            "FROM citas c "
            + "INNER JOIN paciente p ON c.id_paciente = p.id "
            + "ORDER BY c.fecha ASC, c.hora ASC";

    // Formulario Editar Registro
    // ACTUALIZACIONES ESPECIFICAS
    public static final String ACTUALIZAR_NOMBRE = "UPDATE paciente SET nombre = ? WHERE id = ?";
    public static final String ACTUALIZAR_DNI = "UPDATE paciente SET dni = ? WHERE id = ?";
    public static final String ACTUALIZAR_GENERO = "UPDATE paciente SET genero = ? WHERE id = ?";
    public static final String ACTUALIZAR_FECHA = "UPDATE paciente SET fecha_nacimiento = ? WHERE id = ?";

    // ELIMINAR (Ya que tienes el botón, te lo dejo listo)
    public static final String ELIMINAR_PACIENTE = "DELETE FROM paciente WHERE id = ?";

}
