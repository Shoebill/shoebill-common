package net.gtaun.shoebill.common.dialog;

@FunctionalInterface
interface DialogTextSupplier
{
	public static final DialogTextSupplier EMPTY_MESSAGE_SUPPLIER = (d) -> "-";
	
	
	String get(AbstractDialog dialog);
}