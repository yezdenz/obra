# Frontend function reference

This index is extracted from the current Kotlin declarations. See the source links for exact types and bodies. Internal UI helpers are included so teammates can locate event handling.

## Integration entry points

`StudioRepository` supplies the saved snapshot used by screens. `StudioWriter` is implemented by `SqliteStudioRepository`. `ObraApplication` serializes database operations and publishes snapshots. `BaseActivity.write()` coordinates confirmed actions and refreshes.

`Validation.minor()` parses positive integer centavos. `Validation.date()` validates calendar dates and `Validation.schedule()` checks request-date ordering. `Studio.money()` formats display values.

`BaseActivity.refreshData()` loads saved records on resume. Initial read failures expose Retry, and later failures expose Retry refresh while retaining existing information. `confirm()` wraps user confirmation before an operation.

## Source function index

### Models.kt

[Open source](../app/src/main/java/com/obra/studio/data/Models.kt)

- `commissions()`
- `payments()`
- `recommendations(selectedCommissionId: Long)`
- `saveCommission(draft: CommissionDraft, submissionToken: String)`
- `updateCommission(id: Long, draft: CommissionDraft)`
- `recordPayment(commissionId: Long, amountMinor: Long, receivedOn: String, method: String, note: String, submissionToken: String)`
- `startCommission(id: Long, startedOn: String)`
- `updateProgress(id: Long, stage: String, finishedOn: String?)`
- `setTaskDone(commissionId: Long, taskId: Long, done: Boolean)`
- `minor(input: String)`
- `date(input: String)`
- `schedule(d: CommissionDraft, original: Commission?)`
- `day(offset: Long)`
- `work(id: Long)`
- `received(id: Long)`
- `balance(c: Commission)`
- `monthly(month: YearMonth)`
- `money(minor: Long)`

### BaseActivity.kt

[Open source](../app/src/main/java/com/obra/studio/ui/BaseActivity.kt)

- `onCreate(state: Bundle?)`
- `dp(n: Int)`
- `shape(color: Int, radius: Int=22, outline: Boolean=false)`
- `text(value: String, size: Int=15, title: Boolean=false, parent: LinearLayout=body)`
- `button(label: String, parent: LinearLayout=body, primary: Boolean=false, action: ()`
- `card(color: Int=Color.rgb(255,253,249)`
- `title(name: String, back: Boolean=true)`
- `reset(name: String, back: Boolean=true)`
- `section(label: String)`
- `note(value: String)`
- `showObraDialog(title: String, message: String, positive: String,
        negative: String? = null, destructive: Boolean = false, action: ()`
- `error(message: String)`
- `confirm(title: String, message: String, action: ()`
- `detail(id: Long)`
- `form(mode: String="add", id: Long=0)`
- `artwork(c: Commission, parent: LinearLayout=body)`
- `empty(title: String, message: String)`
- `input(label: String, value: String, parent: LinearLayout=body, multiline: Boolean=false)`
- `choose(label: String, values: List<String>, current: String, changed: (String)`
- `onNothingSelected(p: AdapterView<*>?)`
- `onItemSelected(p: AdapterView<*>?,v: View?,position: Int,id: Long)`
- `dateField(label: String, value: String, changed: (String)`

### DetailActivity.kt

[Open source](../app/src/main/java/com/obra/studio/ui/DetailActivity.kt)

- `onStudioReady()`

### FormActivity.kt

[Open source](../app/src/main/java/com/obra/studio/ui/FormActivity.kt)

- `onCreate(state:Bundle?)`
- `handleOnBackPressed()`
- `onSaveInstanceState(out:Bundle)`
- `leave()`
- `field(key:String,label:String,value:String,multi:Boolean=false)`
- `capture()`
- `render()`
- `review(label:String,value:String,target:Int)`
- `validate(until:Int)`
- `payment()`
- `payments()`
- `progress()`

### MainActivity.kt

[Open source](../app/src/main/java/com/obra/studio/ui/MainActivity.kt)

- `onCreate(state: Bundle?)`
- `onSaveInstanceState(out: Bundle)`
- `switch(next: String)`
- `render()`
- `home()`
- `commissions()`
- `populate()`
- `beforeTextChanged(s:CharSequence?,a:Int,c:Int,f:Int)`
- `onTextChanged(s:CharSequence?,a:Int,b:Int,c:Int)`
- `afterTextChanged(e:Editable?)`
- `period()`
- `history()`
- `insights()`
- `reconnect()`
- `task(c:Commission,t:Task)`
- `tasks()`

### ViewerActivity.kt

[Open source](../app/src/main/java/com/obra/studio/ui/ViewerActivity.kt)

- `onCreate(state:Bundle?)`
- `onSaveInstanceState(out:Bundle)`
- `render()`
- `decode(uri: Uri)`
- `onDestroy()`
- `onScale(detector: ScaleGestureDetector)`

## Where to connect actions

- FormActivity: validate/render for Add/Edit; payment for receipt entry; progress for stage/completion.
- DetailActivity: Start confirmation and task checkboxes.
- MainActivity: task toggles and list/report/recommendation reads.
- BaseActivity: detail/form send explicit Intent extras; showObraDialog/error/confirm and refresh feedback handle shared interactions.
- ViewerActivity: image display/gesture handling; final storage lifecycle belongs to the integration layer.

## Storage implementation

- `SqliteStudioRepository.snapshot()` reads commissions, children, receipts and active lookup labels in one consistent transaction.
- `saveCommission`, `updateCommission`, `recordPayment`, `startCommission`, `updateProgress` and `setTaskDone` implement the write contract.
- `ObraDatabaseHelper.onConfigure`, `onCreate`, `onUpgrade` and `migrate1To2` own schema lifecycle.
- `ObraApplication.execute` runs operations off main. `refresh` publishes a saved snapshot.
