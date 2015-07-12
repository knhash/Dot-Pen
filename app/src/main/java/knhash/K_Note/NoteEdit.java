package knhash.K_Note;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.melnykov.fab.FloatingActionButton;

/**
 * Created by Hash on 11-04-2015.
 */
public class NoteEdit extends AppCompatActivity {

    private NotesDbAdapter mDbHelper;
    private EditText mTitleText;
    private EditText mBodyText;
    private EditText mCountText;
    private Long mRowId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDbHelper = new NotesDbAdapter(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //Prevent Keyboard on start
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mDbHelper.open();
        setContentView(R.layout.note_edit);

        mTitleText = (EditText) findViewById(R.id.title);
        mBodyText = (EditText) findViewById(R.id.body);
        mCountText = (EditText) findViewById(R.id.counter);

        /*FloatingActionButton fab1 = (FloatingActionButton) findViewById(R.id.plus);*/

        mRowId = (savedInstanceState == null) ? null :
                (Long) savedInstanceState.getSerializable(NotesDbAdapter.KEY_ROWID);
        if (mRowId == null) {
            Bundle extras = getIntent().getExtras();
            mRowId = extras != null ? extras.getLong(NotesDbAdapter.KEY_ROWID)
                    : null;
        }

        populateFields();

        /*fab1.setOnClickListener(new View.OnClickListener() {

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
            mCountText.setText(note.getString(
                    note.getColumnIndexOrThrow(NotesDbAdapter.KEY_COUNT)));
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
        String count = mCountText.getText().toString();

        if (mRowId == null && !title.equals("")) {
            long id = mDbHelper.createNote(title, body, count);
            if (id > 0) {
                mRowId = id;
            }
            return;
        }

        else if (title.equals("") && !body.equals("")){
            Toast.makeText(this, "Note titled by Dot Pen", Toast.LENGTH_SHORT).show();
            String temptitle = "Note " + mRowId.toString();
            long id = mDbHelper.createNote(temptitle, body, count);
            if (id > 0) {
                mRowId = id;
            }
            return;
        }

        else if (title.equals("") && body.equals("")){
            Toast.makeText(this, "Empty note discarded", Toast.LENGTH_SHORT).show();
            return;
        }

        else {
            mDbHelper.updateNote(mRowId, title, body, count);
            return;
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    CountFunction();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    CountFunction();
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu;
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_note, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        if (id == R.id.COUNT) {
            //createNote();
            CountFunction();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void CountFunction(){
        new MaterialDialog.Builder(this)
                .title("Counter")
                .content(mCountText.getText().toString())
                .titleGravity(GravityEnum.CENTER)
                .contentGravity(GravityEnum.CENTER)
                .buttonsGravity(GravityEnum.CENTER)
                .positiveText("PLUS")
                .negativeText("MINUS")
                .neutralText("ZERO")
                .autoDismiss(false)
                .callback(new MaterialDialog.ButtonCallback() {
                    @Override
                    public void onPositive(MaterialDialog dialog) {
                        String s = mCountText.getText().toString();
                        int count = Integer.parseInt(s);
                        count++;
                        dialog.setContent(String.valueOf(count));
                        mCountText.setText(String.valueOf(count));
                        setResult(RESULT_OK);
                    }

                    @Override
                    public void onNegative(MaterialDialog dialog) {
                        String s = mCountText.getText().toString();
                        int count = Integer.parseInt(s);
                        count--;
                        dialog.setContent(String.valueOf(count));
                        mCountText.setText(String.valueOf(count));
                        setResult(RESULT_OK);
                    }

                    @Override
                    public void onNeutral(MaterialDialog dialog) {
                        int count = 0;
                        dialog.setContent(String.valueOf(count));
                        mCountText.setText(String.valueOf(count));
                        setResult(RESULT_OK);
                    }

                })
                .show();
    }
}
