package com.example.di_ud67_gymfit;

import com.example.di_ud67_gymfit.model.DatosPrueba;
import com.example.di_ud67_gymfit.model.EstadoSocio;
import com.example.di_ud67_gymfit.model.Socio;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.util.StringConverter;
import net.synedra.validatorfx.Check;
import net.synedra.validatorfx.ValidationMessage;
import net.synedra.validatorfx.Validator;
import org.kordamp.ikonli.fontawesome5.FontAwesomeSolid;
import org.kordamp.ikonli.javafx.FontIcon;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** Controlador de la pantalla principal: lista de socios + formulario de alta. */
public class GymFitController {

    /** dd/MM/uuuu en modo estricto: rechaza fechas imposibles como 31/02/2003. */
    private static final DateTimeFormatter FORMATO_FECHA =
            DateTimeFormatter.ofPattern("dd/MM/uuuu").withResolverStyle(ResolverStyle.STRICT);

    private static final String EJEMPLO_FECHA = "por ejemplo 14/05/2003";

    // ---------- Estructura ----------
    @FXML private BorderPane raiz;
    @FXML private Label lblEstado;

    // ---------- Menús y barra de herramientas ----------
    @FXML private MenuItem menuDeshacer;
    @FXML private MenuItem menuEditarSocio;
    @FXML private MenuItem menuCongelar;
    @FXML private MenuItem menuDarDeBaja;
    @FXML private MenuItem menuRenovar;
    @FXML private MenuItem menuPagos;
    @FXML private MenuItem menuImprimirFicha;
    @FXML private Button btnNuevo;
    @FXML private Button btnGuardarBarra;
    @FXML private Button btnDeshacer;
    @FXML private Button btnImprimir;
    @FXML private Button btnAyuda;
    @FXML private Label lblIconoBuscar;
    @FXML private TextField txtBuscar;

    // ---------- Tabla y menú contextual ----------
    @FXML private Label lblTituloTabla;
    @FXML private TableView<Socio> tablaSocios;
    @FXML private TableColumn<Socio, String> colNombre;
    @FXML private TableColumn<Socio, String> colNacimiento;
    @FXML private TableColumn<Socio, String> colProvincia;
    @FXML private TableColumn<Socio, String> colCuota;
    @FXML private TableColumn<Socio, String> colClase;
    @FXML private TableColumn<Socio, String> colEstado;
    @FXML private MenuItem ctxEditar;
    @FXML private MenuItem ctxRenovar;
    @FXML private MenuItem ctxCongelar;
    @FXML private MenuItem ctxPagos;
    @FXML private MenuItem ctxNotas;
    @FXML private MenuItem ctxImprimirFicha;
    @FXML private MenuItem ctxDarDeBaja;

    // ---------- Formulario ----------
    @FXML private Label lblTituloFormulario;
    @FXML private TextField txtNombre;
    @FXML private TextField txtApellidos;
    @FXML private DatePicker dpNacimiento;
    @FXML private ToggleGroup grupoSexo;
    @FXML private ComboBox<String> cbProvincia;
    @FXML private ToggleGroup grupoCuota;
    @FXML private FlowPane paneCuota;
    @FXML private ComboBox<String> cbClase;
    @FXML private CheckBox chkTaquilla;
    @FXML private CheckBox chkToalla;
    @FXML private CheckBox chkParking;
    @FXML private TextField txtIban;
    @FXML private Button btnGuardar;
    @FXML private Button btnBorrarFormulario;

    // ---------- Estado ----------
    private final ObservableList<Socio> socios = FXCollections.observableArrayList(DatosPrueba.socios());
    private final FilteredList<Socio> sociosFiltrados = new FilteredList<>(socios, s -> true);
    private final Validator validador = new Validator();
    /** Cada comprobación con el campo al que se lleva el foco si falla, en orden de arriba abajo. */
    private final Map<Check, Node> comprobaciones = new LinkedHashMap<>();
    private final Deque<Accion> historial = new ArrayDeque<>();
    /** Socio que se está editando, o null si el formulario es de alta. */
    private Socio socioEnEdicion;

    /** Acción que se puede deshacer con Ctrl+Z. */
    private record Accion(String descripcion, Runnable deshacer) {
    }

    @FXML
    private void initialize() {
        configurarIconos();
        configurarTabla();
        configurarFormulario();
        configurarValidacion();
        actualizarDeshacer();
        mostrarEstado("Listo. Pulsa Ctrl+N o «Nuevo» para dar de alta un socio.");
    }

    // =====================================================================
    // Configuración
    // =====================================================================

    private void configurarIconos() {
        btnNuevo.setGraphic(icono(FontAwesomeSolid.USER_PLUS));
        btnGuardarBarra.setGraphic(icono(FontAwesomeSolid.SAVE));
        btnDeshacer.setGraphic(icono(FontAwesomeSolid.UNDO));
        btnImprimir.setGraphic(icono(FontAwesomeSolid.PRINT));
        btnAyuda.setGraphic(icono(FontAwesomeSolid.QUESTION_CIRCLE));
        lblIconoBuscar.setGraphic(icono(FontAwesomeSolid.SEARCH));
        btnGuardar.setGraphic(icono(FontAwesomeSolid.CHECK, "icono-claro"));
        btnBorrarFormulario.setGraphic(icono(FontAwesomeSolid.ERASER, "icono-peligro"));
        ctxDarDeBaja.setGraphic(icono(FontAwesomeSolid.USER_MINUS, "icono-peligro"));
        menuDarDeBaja.setGraphic(icono(FontAwesomeSolid.USER_MINUS, "icono-peligro"));
    }

    private static FontIcon icono(FontAwesomeSolid ikon, String... clases) {
        FontIcon icono = FontIcon.of(ikon, 14);
        icono.getStyleClass().add("icono");
        icono.getStyleClass().addAll(clases);
        return icono;
    }

    private void configurarTabla() {
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombreCompleto()));
        colNacimiento.setCellValueFactory(c -> new SimpleStringProperty(FORMATO_FECHA.format(c.getValue().getFechaNacimiento())));
        colProvincia.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getProvincia()));
        colCuota.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCuota()));
        colClase.setCellValueFactory(c -> new SimpleStringProperty(textoOGuion(c.getValue().getClase())));
        colEstado.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEstado().getTexto()));
        // Ordenar por fecha real, no por el texto dd/mm/aaaa
        colNacimiento.setComparator((a, b) -> LocalDate.parse(a, FORMATO_FECHA).compareTo(LocalDate.parse(b, FORMATO_FECHA)));

        SortedList<Socio> ordenados = new SortedList<>(sociosFiltrados);
        ordenados.comparatorProperty().bind(tablaSocios.comparatorProperty());
        tablaSocios.setItems(ordenados);
        tablaSocios.setPlaceholder(new Label("Ningún socio coincide con la búsqueda."));

        lblTituloTabla.textProperty().bind(Bindings.createStringBinding(() -> {
            long activos = socios.stream().filter(s -> s.getEstado() == EstadoSocio.ACTIVO).count();
            return "Socios (" + sociosFiltrados.size() + ") · " + activos + " activos";
        }, sociosFiltrados, socios));

        // Filas de socios de baja o congelados con estilo distinto; doble clic para editar
        tablaSocios.setRowFactory(tabla -> {
            TableRow<Socio> fila = new TableRow<>() {
                @Override
                protected void updateItem(Socio socio, boolean vacia) {
                    super.updateItem(socio, vacia);
                    getStyleClass().removeAll("fila-baja", "fila-congelada");
                    if (!vacia && socio != null) {
                        if (socio.getEstado() == EstadoSocio.BAJA) getStyleClass().add("fila-baja");
                        if (socio.getEstado() == EstadoSocio.CONGELADO) getStyleClass().add("fila-congelada");
                    }
                }
            };
            fila.setOnMouseClicked(e -> {
                if (e.getButton() == MouseButton.PRIMARY && e.getClickCount() == 2 && !fila.isEmpty()) {
                    cargarEnFormulario(fila.getItem());
                }
            });
            return fila;
        });

        // Supr sobre la tabla = dar de baja (siempre con confirmación)
        tablaSocios.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.DELETE) {
                onDarDeBaja();
                e.consume();
            }
        });

        // Las acciones sobre un socio solo se activan si hay uno seleccionado
        BooleanBinding sinSeleccion = tablaSocios.getSelectionModel().selectedItemProperty().isNull();
        for (MenuItem item : List.of(ctxEditar, ctxRenovar, ctxCongelar, ctxPagos, ctxNotas, ctxImprimirFicha,
                ctxDarDeBaja, menuEditarSocio, menuCongelar, menuDarDeBaja, menuRenovar, menuPagos, menuImprimirFicha)) {
            item.disableProperty().bind(sinSeleccion);
        }

        // Búsqueda en vivo
        txtBuscar.textProperty().addListener((obs, antes, texto) -> {
            String filtro = texto == null ? "" : texto.trim().toLowerCase();
            sociosFiltrados.setPredicate(s -> filtro.isEmpty() || s.getNombreCompleto().toLowerCase().contains(filtro));
        });
        txtBuscar.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.ESCAPE) {
                onMostrarTodos();
                e.consume();
            }
        });
    }

    private void configurarFormulario() {
        dpNacimiento.setConverter(new StringConverter<>() {
            @Override
            public String toString(LocalDate fecha) {
                return fecha == null ? "" : FORMATO_FECHA.format(fecha);
            }

            @Override
            public LocalDate fromString(String texto) {
                return leerFecha(texto);
            }
        });
        // El calendario se abre en una fecha razonable para un adulto, no en hoy
        dpNacimiento.setOnShowing(e -> {
            if (dpNacimiento.getValue() == null && leerFecha(dpNacimiento.getEditor().getText()) == null) {
                dpNacimiento.setValue(LocalDate.now().minusYears(25));
                dpNacimiento.getEditor().clear();
            }
        });

        cbProvincia.setItems(FXCollections.observableArrayList(DatosPrueba.provincias()));
        cbClase.setItems(FXCollections.observableArrayList(DatosPrueba.clases()));
        activarSaltoPorTeclado(cbProvincia);
        activarSaltoPorTeclado(cbClase);
    }

    /** En un desplegable cerrado, escribir «sev» salta a «Sevilla». */
    private static void activarSaltoPorTeclado(ComboBox<String> combo) {
        StringBuilder tecleado = new StringBuilder();
        long[] ultimaTecla = {0};
        combo.setOnKeyTyped(e -> {
            String c = e.getCharacter();
            if (c == null || c.isBlank() || Character.isISOControl(c.charAt(0))) return;
            long ahora = System.currentTimeMillis();
            if (ahora - ultimaTecla[0] > 1000) tecleado.setLength(0);
            ultimaTecla[0] = ahora;
            tecleado.append(c.toLowerCase());
            String prefijo = sinTildes(tecleado.toString());
            combo.getItems().stream()
                    .filter(item -> sinTildes(item.toLowerCase()).startsWith(prefijo))
                    .findFirst()
                    .ifPresent(item -> {
                        combo.getSelectionModel().select(item);
                        if (combo.isShowing()) {
                            combo.hide();
                            combo.show();
                        }
                    });
        });
    }

    private void configurarValidacion() {
        registrar(txtNombre, validador.createCheck()
                .dependsOn("nombre", txtNombre.textProperty())
                .withMethod(c -> {
                    String nombre = c.get("nombre");
                    if (nombre == null || nombre.isBlank()) {
                        c.error("Falta el nombre. Escríbelo tal como aparece en el DNI, por ejemplo: Laura.");
                    }
                }));

        registrar(txtApellidos, validador.createCheck()
                .dependsOn("apellidos", txtApellidos.textProperty())
                .withMethod(c -> {
                    String apellidos = c.get("apellidos");
                    if (apellidos == null || apellidos.isBlank()) {
                        c.error("Faltan los apellidos. Escribe los dos, por ejemplo: Gómez Ruiz.");
                    }
                }));

        registrar(dpNacimiento, validador.createCheck()
                .dependsOn("fecha", dpNacimiento.getEditor().textProperty())
                .withMethod(c -> {
                    String texto = c.get("fecha");
                    if (texto == null || texto.isBlank()) {
                        c.error("Falta la fecha de nacimiento. Elígela en el calendario o escríbela como dd/mm/aaaa, "
                                + EJEMPLO_FECHA + ".");
                        return;
                    }
                    LocalDate fecha = leerFecha(texto);
                    if (fecha == null) {
                        c.error("La fecha de nacimiento «" + texto.trim() + "» no es válida. Escríbela con el formato "
                                + "dd/mm/aaaa, " + EJEMPLO_FECHA + ".");
                    } else if (fecha.isAfter(LocalDate.now())) {
                        c.error("La fecha de nacimiento no puede ser posterior a hoy. Revisa el año, " + EJEMPLO_FECHA + ".");
                    } else if (fecha.isBefore(LocalDate.now().minusYears(110))) {
                        c.error("La fecha de nacimiento es de hace más de 110 años. Revisa el año, " + EJEMPLO_FECHA + ".");
                    }
                }));

        registrar(cbProvincia, validador.createCheck()
                .dependsOn("provincia", cbProvincia.valueProperty())
                .withMethod(c -> {
                    if (c.get("provincia") == null) {
                        c.error("Falta la provincia. Elígela en la lista; puedes escribir las primeras letras, por ejemplo «Sev» para Sevilla.");
                    }
                }));

        registrar(paneCuota, validador.createCheck()
                .dependsOn("cuota", grupoCuota.selectedToggleProperty())
                .withMethod(c -> {
                    if (c.get("cuota") == null) {
                        c.error("Falta el tipo de cuota. Marca una opción: Básica, Plus, Premium, Joven o Senior.");
                    }
                }));

        registrar(txtIban, validador.createCheck()
                .dependsOn("iban", txtIban.textProperty())
                .withMethod(c -> {
                    String iban = c.get("iban");
                    if (iban != null && !iban.isBlank() && !ibanValido(iban)) {
                        c.error("El IBAN no tiene el formato correcto. Debe empezar por ES seguido de 22 números, "
                                + "por ejemplo ES12 3456 7890 1234 5678 9012. Si no lo tienes, déjalo vacío.");
                    }
                }));
    }

    /** Las comprobaciones se muestran junto al campo y se limpian en cuanto el usuario lo corrige. */
    private void registrar(Node campo, Check check) {
        check.decorates(campo).immediateClear();
        comprobaciones.put(check, campo);
    }

    // =====================================================================
    // Acciones del formulario
    // =====================================================================

    @FXML
    private void onNuevo() {
        limpiarFormulario();
        txtNombre.requestFocus();
        mostrarEstado("Nuevo socio: rellena el formulario y pulsa «Guardar socio» (Ctrl+S).");
    }

    @FXML
    private void onGuardar() {
        if (!validador.validate()) {
            mostrarErroresDeValidacion();
            return;
        }

        Socio datos = new Socio(
                txtNombre.getText().trim(),
                txtApellidos.getText().trim(),
                leerFecha(dpNacimiento.getEditor().getText()),
                textoSeleccionado(grupoSexo),
                cbProvincia.getValue(),
                textoSeleccionado(grupoCuota),
                cbClase.getValue(),
                extrasMarcados(),
                normalizarIban(txtIban.getText()),
                EstadoSocio.ACTIVO);

        if (socioEnEdicion == null) {
            socios.add(datos);
            historial.push(new Accion("alta de " + datos.getNombreCompleto(), () -> socios.remove(datos)));
            seleccionar(datos);
            mostrarEstado("Socio dado de alta: " + datos.getNombreCompleto() + ". Ctrl+Z para deshacer.");
        } else {
            Socio editado = socioEnEdicion;
            Socio antes = editado.copiar();
            datos.setEstado(antes.getEstado());
            editado.copiarDe(datos);
            historial.push(new Accion("cambios en " + editado.getNombreCompleto(), () -> editado.copiarDe(antes)));
            tablaSocios.refresh();
            seleccionar(editado);
            mostrarEstado("Cambios guardados en " + editado.getNombreCompleto() + ". Ctrl+Z para deshacer.");
        }
        actualizarDeshacer();
        limpiarFormulario();
    }

    @FXML
    private void onCancelar() {
        boolean estabaEditando = socioEnEdicion != null;
        limpiarFormulario();
        mostrarEstado(estabaEditando ? "Edición cancelada. No se ha cambiado nada." : "Formulario vacío. No se ha guardado nada.");
    }

    @FXML
    private void onBorrarFormulario() {
        if (formularioVacio()) {
            mostrarEstado("El formulario ya está vacío.");
            return;
        }
        if (confirmar("¿Borrar todos los datos del formulario?",
                "Se perderán los datos que has escrito y aún no has guardado. Los socios de la tabla no se tocan.",
                "Borrar formulario")) {
            limpiarFormulario();
            mostrarEstado("Formulario borrado.");
        }
    }

    // =====================================================================
    // Acciones sobre el socio seleccionado (menú contextual y menús)
    // =====================================================================

    @FXML
    private void onEditar() {
        Socio socio = seleccionado();
        if (socio != null) cargarEnFormulario(socio);
    }

    @FXML
    private void onDarDeBaja() {
        Socio socio = seleccionado();
        if (socio == null) {
            mostrarEstado("Selecciona primero en la tabla el socio que quieres dar de baja.");
            return;
        }
        if (socio.getEstado() == EstadoSocio.BAJA) {
            mostrarEstado(socio.getNombreCompleto() + " ya está de baja.");
            return;
        }
        if (confirmar("¿Dar de baja a " + socio.getNombreCompleto() + "?",
                "Dejará de figurar como socio activo y no se le cobrarán más cuotas. "
                        + "Si te equivocas, puedes deshacerlo con Editar ▸ Deshacer (Ctrl+Z).",
                "Dar de baja")) {
            cambiarEstado(socio, EstadoSocio.BAJA, "baja de " + socio.getNombreCompleto());
            mostrarEstado(socio.getNombreCompleto() + " ha sido dado de baja. Ctrl+Z para deshacer.");
        }
    }

    @FXML
    private void onCongelar() {
        Socio socio = seleccionado();
        if (socio == null) return;
        if (socio.getEstado() == EstadoSocio.BAJA) {
            mostrarEstado(socio.getNombreCompleto() + " está de baja: no se puede congelar.");
            return;
        }
        boolean congelar = socio.getEstado() != EstadoSocio.CONGELADO;
        cambiarEstado(socio, congelar ? EstadoSocio.CONGELADO : EstadoSocio.ACTIVO,
                (congelar ? "congelación de " : "reactivación de ") + socio.getNombreCompleto());
        mostrarEstado(socio.getNombreCompleto() + (congelar ? " tiene la cuota congelada." : " vuelve a estar activo.")
                + " Ctrl+Z para deshacer.");
    }

    @FXML
    private void onRenovar() {
        Socio socio = seleccionado();
        if (socio == null) return;
        if (socio.getEstado() != EstadoSocio.ACTIVO) {
            cambiarEstado(socio, EstadoSocio.ACTIVO, "renovación de " + socio.getNombreCompleto());
        }
        mostrarEstado("Cuota " + socio.getCuota() + " de " + socio.getNombreCompleto() + " renovada.");
    }

    @FXML
    private void onPagos() {
        Socio socio = seleccionado();
        if (socio != null) noIncluidoEnPrototipo("Pagos de " + socio.getNombreCompleto());
    }

    @FXML
    private void onNotas() {
        Socio socio = seleccionado();
        if (socio != null) noIncluidoEnPrototipo("Notas de " + socio.getNombreCompleto());
    }

    @FXML
    private void onImprimirFicha() {
        Socio socio = seleccionado();
        if (socio != null) noIncluidoEnPrototipo("Imprimir la ficha de " + socio.getNombreCompleto());
    }

    // =====================================================================
    // Menús generales
    // =====================================================================

    @FXML
    private void onDeshacer() {
        Accion accion = historial.poll();
        if (accion == null) {
            mostrarEstado("No hay nada que deshacer.");
            return;
        }
        accion.deshacer().run();
        tablaSocios.refresh();
        actualizarDeshacer();
        mostrarEstado("Deshecho: " + accion.descripcion() + ".");
    }

    @FXML
    private void onCortar() {
        if (raiz.getScene().getFocusOwner() instanceof TextInputControl campo) campo.cut();
    }

    @FXML
    private void onCopiar() {
        if (raiz.getScene().getFocusOwner() instanceof TextInputControl campo) campo.copy();
    }

    @FXML
    private void onPegar() {
        if (raiz.getScene().getFocusOwner() instanceof TextInputControl campo) campo.paste();
    }

    @FXML
    private void onBuscar() {
        txtBuscar.requestFocus();
        txtBuscar.selectAll();
    }

    @FXML
    private void onMostrarTodos() {
        txtBuscar.clear();
        tablaSocios.requestFocus();
    }

    @FXML
    private void onImprimir() {
        noIncluidoEnPrototipo("Imprimir el listado de socios");
    }

    @FXML
    private void onAyuda() {
        Alert ayuda = new Alert(Alert.AlertType.INFORMATION);
        ayuda.initOwner(raiz.getScene().getWindow());
        ayuda.setTitle("Ayuda · GymFit Manager");
        ayuda.setHeaderText("Atajos de teclado");
        ayuda.setContentText("""
                Ctrl+N    Nuevo socio
                Ctrl+S    Guardar socio (también Intro)
                Ctrl+E    Editar el socio seleccionado (o doble clic)
                Supr      Dar de baja al socio seleccionado (pide confirmación)
                Ctrl+Z    Deshacer la última acción
                Ctrl+F    Buscar socio  ·  Esc: ver todos
                Esc       Cancelar el formulario
                F1        Esta ayuda""");
        ayuda.showAndWait();
    }

    @FXML
    private void onAcercaDe() {
        Alert acerca = new Alert(Alert.AlertType.INFORMATION);
        acerca.initOwner(raiz.getScene().getWindow());
        acerca.setTitle("Acerca de GymFit Manager");
        acerca.setHeaderText("GymFit Manager · prototipo de rediseño");
        acerca.setContentText("Desarrollo de Interfaces · Temas 6 y 7.\nLos datos son de prueba y se pierden al cerrar.");
        acerca.showAndWait();
    }

    @FXML
    private void onSalir() {
        Platform.exit();
    }

    // =====================================================================
    // Mensajes
    // =====================================================================

    /** Mensaje de error reescrito: qué ha pasado y cómo solucionarlo, sin códigos ni culpas. */
    private void mostrarErroresDeValidacion() {
        List<String> mensajes = validador.getValidationResult().getMessages().stream()
                .map(ValidationMessage::getText)
                .collect(Collectors.toList());

        Alert alerta = new Alert(Alert.AlertType.WARNING);
        alerta.initOwner(raiz.getScene().getWindow());
        alerta.setTitle("Faltan datos para guardar");
        alerta.setHeaderText(mensajes.size() == 1
                ? "El socio no se ha guardado: hay 1 dato por revisar"
                : "El socio no se ha guardado: hay " + mensajes.size() + " datos por revisar");
        alerta.setContentText(mensajes.stream().map(m -> "• " + m).collect(Collectors.joining("\n\n"))
                + "\n\nLos campos a revisar están marcados en el formulario.");
        alerta.getButtonTypes().setAll(new ButtonType("Corregir", ButtonBar.ButtonData.OK_DONE));
        alerta.getDialogPane().setPrefWidth(520);
        alerta.showAndWait();

        // Llevar el foco al primer campo con error (de arriba abajo)
        comprobaciones.entrySet().stream()
                .filter(e -> !e.getKey().getValidationResult().getMessages().isEmpty())
                .findFirst()
                .ifPresent(e -> e.getValue().requestFocus());
        mostrarEstado("Corrige los campos marcados y vuelve a pulsar «Guardar socio».");
    }

    /** Confirmación de una acción destructiva. El botón por defecto es «Cancelar». */
    private boolean confirmar(String pregunta, String explicacion, String textoAccion) {
        ButtonType accion = new ButtonType(textoAccion, ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alerta = new Alert(Alert.AlertType.CONFIRMATION, explicacion, accion, cancelar);
        alerta.initOwner(raiz.getScene().getWindow());
        alerta.setTitle("Confirmar");
        alerta.setHeaderText(pregunta);
        Button botonAccion = (Button) alerta.getDialogPane().lookupButton(accion);
        Button botonCancelar = (Button) alerta.getDialogPane().lookupButton(cancelar);
        botonAccion.setDefaultButton(false);
        botonAccion.getStyleClass().add("boton-peligro");
        botonCancelar.setDefaultButton(true);
        alerta.getDialogPane().getStylesheets().addAll(raiz.getStylesheets());
        return alerta.showAndWait().filter(b -> b == accion).isPresent();
    }

    private void noIncluidoEnPrototipo(String funcion) {
        mostrarEstado(funcion + ": esta opción no está incluida en el prototipo.");
    }

    private void mostrarEstado(String texto) {
        lblEstado.setText(texto);
    }

    // =====================================================================
    // Utilidades
    // =====================================================================

    private void cargarEnFormulario(Socio socio) {
        limpiarFormulario();
        socioEnEdicion = socio;
        txtNombre.setText(socio.getNombre());
        txtApellidos.setText(socio.getApellidos());
        dpNacimiento.setValue(socio.getFechaNacimiento());
        seleccionarPorTexto(grupoSexo, socio.getSexo());
        cbProvincia.setValue(socio.getProvincia());
        seleccionarPorTexto(grupoCuota, socio.getCuota());
        cbClase.setValue(socio.getClase());
        chkTaquilla.setSelected(socio.getExtras().contains(chkTaquilla.getText()));
        chkToalla.setSelected(socio.getExtras().contains(chkToalla.getText()));
        chkParking.setSelected(socio.getExtras().contains(chkParking.getText()));
        txtIban.setText(socio.getIban() == null ? "" : socio.getIban());
        lblTituloFormulario.setText("Editar socio: " + socio.getNombreCompleto());
        btnGuardar.setText("Guardar cambios");
        txtNombre.requestFocus();
        mostrarEstado("Editando a " + socio.getNombreCompleto() + ". Pulsa «Guardar cambios» o Esc para cancelar.");
    }

    private void limpiarFormulario() {
        socioEnEdicion = null;
        txtNombre.clear();
        txtApellidos.clear();
        dpNacimiento.setValue(null);
        dpNacimiento.getEditor().clear();
        grupoSexo.selectToggle(null);
        cbProvincia.setValue(null);
        grupoCuota.selectToggle(null);
        cbClase.setValue(null);
        chkTaquilla.setSelected(false);
        chkToalla.setSelected(false);
        chkParking.setSelected(false);
        txtIban.clear();
        lblTituloFormulario.setText("Alta de socio nuevo");
        btnGuardar.setText("Guardar socio");
    }

    private boolean formularioVacio() {
        return txtNombre.getText().isBlank() && txtApellidos.getText().isBlank()
                && dpNacimiento.getEditor().getText().isBlank() && grupoSexo.getSelectedToggle() == null
                && cbProvincia.getValue() == null && grupoCuota.getSelectedToggle() == null
                && cbClase.getValue() == null && extrasMarcados().isEmpty() && txtIban.getText().isBlank();
    }

    private void cambiarEstado(Socio socio, EstadoSocio nuevo, String descripcion) {
        EstadoSocio anterior = socio.getEstado();
        socio.setEstado(nuevo);
        historial.push(new Accion(descripcion, () -> socio.setEstado(anterior)));
        actualizarDeshacer();
        tablaSocios.refresh();
        // Fuerza a recalcular el contador de activos del título
        socios.set(socios.indexOf(socio), socio);
        seleccionar(socio);
    }

    private void actualizarDeshacer() {
        boolean vacio = historial.isEmpty();
        menuDeshacer.setDisable(vacio);
        btnDeshacer.setDisable(vacio);
        menuDeshacer.setText(vacio ? "Deshacer" : "Deshacer " + historial.peek().descripcion());
    }

    private Socio seleccionado() {
        return tablaSocios.getSelectionModel().getSelectedItem();
    }

    private void seleccionar(Socio socio) {
        tablaSocios.getSelectionModel().select(socio);
        tablaSocios.scrollTo(socio);
    }

    private List<String> extrasMarcados() {
        List<String> extras = new ArrayList<>();
        for (CheckBox casilla : List.of(chkTaquilla, chkToalla, chkParking)) {
            if (casilla.isSelected()) extras.add(casilla.getText());
        }
        return extras;
    }

    private static String textoSeleccionado(ToggleGroup grupo) {
        Toggle t = grupo.getSelectedToggle();
        return t == null ? null : ((RadioButton) t).getText();
    }

    private static void seleccionarPorTexto(ToggleGroup grupo, String texto) {
        grupo.selectToggle(grupo.getToggles().stream()
                .filter(t -> ((RadioButton) t).getText().equals(texto))
                .findFirst().orElse(null));
    }

    private static LocalDate leerFecha(String texto) {
        if (texto == null || texto.isBlank()) return null;
        try {
            return LocalDate.parse(texto.trim(), FORMATO_FECHA);
        } catch (DateTimeParseException e) {
            return null;
        }
    }

    /** IBAN español: ES + 22 dígitos (se admiten espacios). */
    private static boolean ibanValido(String iban) {
        return iban.replace(" ", "").toUpperCase().matches("ES\\d{22}");
    }

    /** Guarda el IBAN agrupado de 4 en 4 para que se lea mejor. */
    private static String normalizarIban(String iban) {
        if (iban == null || iban.isBlank()) return null;
        String limpio = iban.replace(" ", "").toUpperCase();
        return limpio.replaceAll("(.{4})(?!$)", "$1 ");
    }

    private static String textoOGuion(String texto) {
        return texto == null || texto.isBlank() ? "—" : texto;
    }

    private static String sinTildes(String texto) {
        return java.text.Normalizer.normalize(texto, java.text.Normalizer.Form.NFD).replaceAll("\\p{M}", "");
    }
}
