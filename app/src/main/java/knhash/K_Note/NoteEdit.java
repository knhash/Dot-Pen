package knhash.K_Note;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;

import com.melnykov.fab.FloatingActionButton;

/**
 * Created by Hash on 11-04-2015.
 */
public class NoteEdit extends ActionBarActivity {

    private NotesDbAdapter mDbHelper;
    private EditText mTitleText;
    private EditText mBodyText;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);

        //Prevent Keyboard on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mDbHelper.open();
        setContentView(R.layout.note_edit);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);

        //Button confirmButton = (Button) findViewById(R.id.confirm);
        FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.plus);
        FloatingActionButton fab2 = (FloatingActionButton) findViewById(R.id.minus);
        FloatingActionButton fab3 = (FloatingActionButton) findViewById(R.id.zero);

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();

        fab2.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String s = mBodyText.getText().toString();
                int count = Integer.parseInt(s);
                count--;
                mBodyText.setText(String.valueOf(count));
                setResult(RESULT_OK);
                //finish();
            }

        });

        fab1.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String s = mBodyText.getText().toString();
                int count = Integer.parseInt(s);
                count++;
                mBodyText.setText(String.valueOf(count));
                setResult(RESULT_OK);
                //finish();
            }

        });

        fab3.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String s = mBodyText.getText().toString();
                int count = 0;
                mBodyText.setText(String.valueOf(count));
                setResult(RESULT_OK);
                //finish();
            }

        });

        /*confirmButton.setOnClickListener(new View.OnClickListener() {

            public void onClick(View view) {
                String s = mBodyText.getText().toString();
                int count = Integer.parseInt(s);
                count++;
                mBodyText.setText(String.valueOf(count));
                setResult(RESULT_OK);
                //finish();
            }

        });*/
    }

    private void populateFields() {
        if (mRowId != null) {
            Cursor note = mDbHelper.fetchNote(mRowId);
            startManagingCursor(note);
            mTitleText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_TITLE)));
            mBodyText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_BODY)));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveState();
        outState.putSerializable(NotesDbAdapter.KEY_ROWID, mRowId);
    }

    @Override
    protected void onPause() {
        super.onPause();
        saveState();
    }

    @Override
    protected void onResume() {
        super.onResume();
        populateFields();
    }

    private void saveState() {
        String title = mTitleText.getText().toString();
        String body = mBodyText.getText().toString();

        if (mRowId == null && !title.equals("")) {
            long id = mDbHelper.createNote(title, body);
            if (id > 0) {
                mRowId = id;
            }
        }

        else if (title.equals("")){
            return;
        }

        else {
            mDbHelper.updateNote(mRowId, title, body);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    String s = mBodyText.getText().toString();
                    int count = Integer.parseInt(s);
                    count++;
                    mBodyText.setText(String.valueOf(count));
                    setResult(RESULT_OK);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    String s = mBodyText.getText().toString();
                    int count = Integer.parseInt(s);
                    count--;
                    mBodyText.setText(String.valueOf(count));
                    setResult(RESULT_OK);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }
}
