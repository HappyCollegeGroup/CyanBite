package fcu.app.cyanbite.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

import fcu.app.cyanbite.R;
import fcu.app.cyanbite.adapter.OrderGroupListAdapter;
import fcu.app.cyanbite.model.Group;

public class SearchActivity extends AppCompatActivity {
    private Button btnReturn;
    private EditText etSearch;
    private RecyclerView rvSearch;
    private OrderGroupListAdapter resultAdapter;
    private FirebaseFirestore db;
    private List<Group> resultList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_search);

        initFirebase();
        initView();
        setupListener();
        setupRecyclerView();
    }

    private void initFirebase() {
        db = FirebaseFirestore.getInstance();
    }

    private void initView() {
        btnReturn = findViewById(R.id.btn_return);
        etSearch = findViewById(R.id.et_search);
        rvSearch = findViewById(R.id.rv_search);

        db.collection("group")
                .whereArrayContains("searchKeywords", "")
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    resultList.clear();
                    List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                    for (int j = 0; j < docs.size(); j++) {
                        DocumentSnapshot doc = docs.get(j);
                        String name = doc.getString("name");
                        String description = doc.getString("description");
                        String image = doc.getString("image");
                        resultList.add(new Group(name, description, "", "", "", "", null, image));
                    }
                    resultAdapter.notifyDataSetChanged();
                });
    }

    private void setupListener() {
        btnReturn.setOnClickListener(view -> {
            finish();
        });

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                db.collection("group")
                        .whereArrayContains("searchKeywords", charSequence.toString())
                        .get()
                        .addOnSuccessListener(queryDocumentSnapshots -> {
                            resultList.clear();
                            List<DocumentSnapshot> docs = queryDocumentSnapshots.getDocuments();
                            for (int j = 0; j < docs.size(); j++) {
                                DocumentSnapshot doc = docs.get(j);
                                String name = doc.getString("name");
                                String description = doc.getString("description");
                                String image = doc.getString("image");
                                resultList.add(new Group(name, description, "", "", "", "", null, image));
                            }
                            resultAdapter.notifyDataSetChanged();
                        });
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void setupRecyclerView() {
        rvSearch.setLayoutManager(new LinearLayoutManager(this));
        resultAdapter = new OrderGroupListAdapter(this, resultList);
        rvSearch.setAdapter(resultAdapter);
    }
}