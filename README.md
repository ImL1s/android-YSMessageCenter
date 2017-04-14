# android-ys-messageCenter


## a lite message center not use reflecter


### simple use

addListener

    private void addListener() {
        MessageCenter.init(getApplicationContext(), MessageCenter.PollingMode.BACKGROUND);

        MessageCenter.getInstance().addListener("onclick", new MessageCenter.MessageEventListener() {
            @Override
            public void onEvent(MessageCenter.Message msg) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "onclick", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

dispatch event

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            MessageCenter.getInstance().sendEmpty("onclick");
            return true;
        } else
            return super.onTouchEvent(event);
    }
    
    
### sticky dispatch

dispatch event

    private void testStickyMsgCenter() {
        MessageCenter.init(getApplicationContext(), MessageCenter.PollingMode.BACKGROUND);
        MessageCenter.getInstance().sendEmptySticky("onclick");
    }
    
add listener

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            MessageCenter.getInstance().addListener("onclick", new MessageCenter.MainThreadEventListener() {
                @Override
                public void onEvent(MessageCenter.Message msg) {
                    Toast.makeText(MainActivity.this, "onclick", Toast.LENGTH_LONG).show();
                }
            }, true);

            return true;
        } else
            return super.onTouchEvent(event);
    }


