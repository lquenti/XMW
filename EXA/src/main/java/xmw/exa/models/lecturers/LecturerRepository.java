package xmw.exa.models.lecturers;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.basex.core.BaseXException;
import org.basex.core.Context;
import org.basex.core.cmd.XQuery;
import xmw.exa.db.DB;
import xmw.exa.db.ExaElement;
import xmw.exa.db.repository.BaseXmlRepository;
import xmw.exa.responses.User;
import xmw.exa.responses.Users;
import xmw.exa.util.Util;
import xmw.flush.Firstname;
import xmw.flush.Lecturer;
import xmw.flush.Lecturers;
import xmw.flush.Name;

import java.util.List;

public class LecturerRepository extends BaseXmlRepository<Lecturer> {
    OkHttpClient httpClient = new OkHttpClient();

    public LecturerRepository(Context context) {
        super(context, Lecturers.class, Lecturer.class);
    }

    @Override
    public List<Lecturer> all() {
        try {
            // TODO: fetch all from /user/bulk
//            var users = fetchFromUserEndpoint();
//
//            List<Lecturer> newLecturers = users.stream().map(user -> {
//                var lecturer = new Lecturer();
//                lecturer.setId(user.getUsername());
//                lecturer.setFaculty(user.getFaculty());
//                ((Firstname) lecturer.getFirstnameOrName().get(0)).setContent(user.getFirstname());
//                ((Name) lecturer.getFirstnameOrName().get(1)).setContent(user.getName());
//                return lecturer;
//            }).toList();
            var rawUsers = fetchFromUserEndpointRaw();
            // replace <Users> with <Lecturers>
            rawUsers = rawUsers.replace("<Users>", "<Lecturers>");
            rawUsers = rawUsers.replace("</Users>", "</Lecturers>");
            // replace <User> with <Lecturer>
            rawUsers = rawUsers.replace("User", "Lecturer");

            var tmpLects = (Lecturers) DB.unmarshal(rawUsers, Lecturers.class);

            for (Lecturer l : tmpLects.getLecturer()) {
                var users = fetchFromUserEndpoint();
                for (User u : users) {
                    if (l.getUsername().equals(u.getUsername())) {
                        l.setFaculty(u.getFaculty());
                    }
                }
            }


            var root = DB.getRootChildMap(context);
            Lecturers lecturers = (Lecturers) root.get(ExaElement.LECTURERS);

            for (Lecturer l : tmpLects.getLecturer()) {
                boolean found = false;
                for (Lecturer l2 : lecturers.getLecturer()) {
                    if (l.getUsername().equals(l2.getUsername())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    this.create(l);
                    lecturers.getLecturer().add(l);
                }
            }

            root = DB.getRootChildMap(context);
            lecturers = (Lecturers) root.get(ExaElement.LECTURERS);


            return lecturers.getLecturer();
        } catch (BaseXException e) {
            throw new RuntimeException(e);
        }
    }

    private String fetchFromUserEndpointRaw() {
        Request request = new Request.Builder()
                .url("http://localhost:8080/user/bulk")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch data from /user/bulk");
            }
            assert response.body() != null;
            return response.body().string();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<User> fetchFromUserEndpoint() {
        Request request = new Request.Builder()
                .url("http://localhost:8080/user/bulk")
                .build();
        try (Response response = httpClient.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new RuntimeException("Failed to fetch data from /user/bulk");
            }
            assert response.body() != null;
            String xmlResponseBody = response.body().string();
            return Users.getUsers(xmlResponseBody);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}