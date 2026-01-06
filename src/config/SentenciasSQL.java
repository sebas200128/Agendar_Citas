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

    // LISTAR CITAS EN TABLA (Para poder seleccionarlas con clic)
    public static final String LISTAR_CITAS_TABLA
            = "SELECT c.id, p.nombre, c.fecha, c.hora, p.id as id_paciente "
            + "FROM citas c "
            + "INNER JOIN paciente p ON c.id_paciente = p.id "
            + "ORDER BY c.fecha ASC, c.hora ASC";

    // ACTUALIZAR CITA
    public static final String ACTUALIZAR_CITA
            = "UPDATE citas SET id_paciente = ?, fecha = ?, hora = ? WHERE id = ?";

    // BASE DE LA CONSULTA (Sin el Order By)
    private static final String BASE_SELECT_CITAS
            = "SELECT c.id, p.nombre, c.fecha, c.hora, p.id as id_paciente "
            + "FROM citas c "
            + "INNER JOIN paciente p ON c.id_paciente = p.id ";

    // 1. ORDENAR POR FECHA (Lo normal, incluye hora)
    public static final String ORDER_FECHA = BASE_SELECT_CITAS + "ORDER BY c.fecha ASC, c.hora ASC";

    // 2. ORDENAR POR NOMBRE (Alfabético)
    public static final String ORDER_NOMBRE = BASE_SELECT_CITAS + "ORDER BY p.nombre ASC, c.fecha ASC";

    // 3. ORDENAR POR MES (Agrupa por mes de la fecha)
    // Usamos la función MONTH() de MySQL
    public static final String ORDER_MES = BASE_SELECT_CITAS + "ORDER BY MONTH(c.fecha) ASC, c.fecha ASC";

}
