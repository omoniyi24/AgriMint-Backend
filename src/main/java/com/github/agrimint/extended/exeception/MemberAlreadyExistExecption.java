package com.github.agrimint.extended.exeception;

/**
 * @author OMONIYI ILESANMI
 */
public class MemberAlreadyExistExecption extends IllegalArgumentException {

    public MemberAlreadyExistExecption(String message) {
        super(message);
    }
}
