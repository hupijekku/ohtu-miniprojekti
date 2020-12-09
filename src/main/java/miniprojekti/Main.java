package miniprojekti;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import miniprojekti.domain.BlogpostTip;
import miniprojekti.domain.BookTip;
import miniprojekti.domain.Logic;
import miniprojekti.domain.PodcastTip;
import miniprojekti.domain.Tip;
import miniprojekti.domain.Validation;
import miniprojekti.domain.VideoTip;
import spark.ModelAndView;
import spark.Request;
import spark.Spark;
import static spark.Spark.before;
import static spark.Spark.get;
import static spark.Spark.post;
import spark.template.velocity.VelocityTemplateEngine;

// Main tulee toimimaan Controllerina
public class Main {

    private final static Logic LOGIC = new Logic();
    private static final String LAYOUT = "templates/layout.html";
    private final static Validation VALIDATOR = new Validation();
    private static final String SESSION_NAME = "session";
    private static final String USER_ID = "user_id";

    public static void main(String[] args) {
        Spark.port(portSelection());
        
        checkSession();
        getLoginPage();
        handleLogin();
        register();
        getIndexPage();
        postReadingTip();
        getReadingTipsPage();
        addReadingTipPage();
        singleTipPage();
        deleteTip();
        editTip();
    }
    
    private static void checkSession() {
        before((req, res) -> {
            String session = req.session().attribute(SESSION_NAME);
            if (session == null && !req.pathInfo().equals("/")) {
                res.redirect("/");
            }
        });
    }
    
    private static void getLoginPage() {
        get("/", (req, res) -> {
            String session = req.session().attribute(SESSION_NAME);
            if (session != null) {
                res.redirect("/index");
            }
            HashMap<String, Object> model = new HashMap<>();
            model.put("template", "templates/login.html");
            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());
    }
    
    private static void handleLogin() {
        post("/login", (req, res) ->  {
            HashMap<String, Object> model = new HashMap<>();
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            int user_id = LOGIC.validateUser(username, password);
            if (user_id > 0) {
                req.session().attribute(SESSION_NAME, username);
                req.session().attribute(USER_ID, user_id);
                model.put("logged", "Successfully logged in as " + username);
                model.put("template", "templates/index.html");
            } else {
                model.put("logged", "Incorrect username or password");
                model.put("template", "templates/login.html");
            }
            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());
    }
    
    private static void register() {
        post("/register", (req, res) ->  {
            String username = req.queryParams("username");
            String password = req.queryParams("password");
            LOGIC.registerUser(username, password);
            HashMap<String, Object> model = new HashMap<>();
            model.put("template", "templates/login.html");
            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());
    }

    private static void getIndexPage() {
        get("/index", (req, res) -> {
            HashMap<String, Object> model = new HashMap<>();
            String types = "All";
            try {
                String query = req.queryParams("typesToShow");
                if (!query.isEmpty()) {
                    types = query;
                }
            } catch (Exception e) {
            }
            
            model.put("template", "templates/index.html");
            int user_id = req.session().attribute(USER_ID);
            model.put("tips", LOGIC.retrieveAllTipsByType(types, user_id));
            
            return new ModelAndView(model, LAYOUT);
        },new VelocityTemplateEngine());

    }

    private static void deleteTip() {
        get("/tips/delete/:id", (req, res) -> {
            HashMap<String, Object> model = new HashMap<>();
            model.put("template", "templates/index.html");
            String s = req.params(":id");
            int id = Integer.parseInt(s);
            LOGIC.deleteTipByID(id);
            model.put("deleted", "Tip deleted!");
            res.redirect("/");
            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());
    }

    private static void editTip() {
        post("/tips/:id/:type", (req, res) -> {
            HashMap<String, Object> model = new HashMap<>();
            model.put("template", "templates/index.html");

            Map<String, String> paramMap = getQueryParams(req);
            
            int id = Integer.parseInt(req.params(":id"));
            
            String type = req.params(":type");
            paramMap.put("type", type);
            paramMap.put("id", String.valueOf(id));
            
            ArrayList<String> validationMessages = VALIDATOR.validate(paramMap);
            
            if (!validationMessages.isEmpty()) {
                model.put("template", "templates/tip.html");
                
                List<Map<String, String>> listForTip = new ArrayList<>();
                listForTip.add(paramMap);
                model.put("tips", listForTip);
                
                model.put("errors", validationMessages);
            } else {
                Tip tip;
                switch (type) {
                    case "Book":
                        tip = new BookTip(id, req.queryParams("title"), req.queryParams("note"), 0,
                                req.queryParams("author"), req.queryParams("isbn"), req.queryParams("url"));
                        LOGIC.updateTip(tip);
                        break;
                    case "Video":
                        tip = new VideoTip(id, req.queryParams("title"), req.queryParams("note"), 0,
                                req.queryParams("url"));
                        LOGIC.updateTip(tip);
                        break;
                    case "Podcast":
                        tip = new PodcastTip(id, req.queryParams("title"), req.queryParams("note"), 0,
                                req.queryParams("author"), req.queryParams("description"), req.queryParams("url"));
                        LOGIC.updateTip(tip);
                        break;
                    case "Blogpost":
                        tip = new BlogpostTip(id, req.queryParams("title"), req.queryParams("note"), 0,
                                req.queryParams("url"));
                        LOGIC.updateTip(tip);
                        break;
                    default:
                        break;
                }
                model.put("editedTip", "Tip updated!");
                int user_id = req.session().attribute(USER_ID);
                model.put("tips", LOGIC.retrieveAllTips(user_id));
            }     
            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());
    }

    private static void postReadingTip() {
        post("/add/:type", (req, res) -> {
            HashMap<String, Object> model = new HashMap<>();

            String type = req.params("type");
            
            Map<String, String> paramMap = getQueryParams(req);
            paramMap.put("type", type);

            ArrayList<String> validationMessages = VALIDATOR.validate(paramMap);
            
            if (!validationMessages.isEmpty()) {
                model.put("errors", validationMessages);
                model.put("template", "templates/add.html");
                model.put("type", type);
            } else {
                int user_id = req.session().attribute(USER_ID);
                LOGIC.saveNewTip(paramMap, user_id);
                
                model.put("template", "templates/index.html");
                model.put("tipAdded", "New tip added succesfully");
                model.put("tips", LOGIC.retrieveAllTips(user_id));
            }

            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());

    }

    private static void getReadingTipsPage() {
        get("/tips", (req, res) -> {
            HashMap<String, Object> model = new HashMap<>();

            int user_id = req.session().attribute(USER_ID);
            model.put("tips", LOGIC.retrieveAllTips(user_id));
            model.put("template", "templates/tips.html");
            
            ArrayList<String> errors = new ArrayList<>();
            errors.add("111111111111");
            model.put("errors", errors);

            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());
    }

    private static void singleTipPage() {
        get("/tips/:id", (req, res) -> {
            HashMap<String, Object> model = new HashMap<>();

            String id = req.params(":id");
            int user_id = req.session().attribute(USER_ID);
            model.put("tips", LOGIC.retrieveTip(id, user_id));
            model.put("template", "templates/tip.html");

            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());
    }

    private static void addReadingTipPage() {
        get("/add", (req, res) -> {
            HashMap<String, Object> model = new HashMap<>();

            String tipType = req.queryParams("tipTypes");

            model.put("type", tipType);
            model.put("template", "templates/add.html");

            return new ModelAndView(model, LAYOUT);
        }, new VelocityTemplateEngine());
    }

    private static Integer portSelection() {
        ProcessBuilder process = new ProcessBuilder();
        if (process.environment().get("PORT") != null) {
            return Integer.parseInt(process.environment().get("PORT"));
        }
        return 4567;
    }

    private static Map<String, String> getQueryParams(Request request) {
        final Map<String, String> paramMap = new HashMap<>();

        request.queryMap().toMap().forEach((key, value) -> {
            paramMap.put(key, value[0]);
        });

        return paramMap;
    }
}