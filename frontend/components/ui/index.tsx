import {
  ButtonHTMLAttributes,
  InputHTMLAttributes,
  ReactNode,
  SelectHTMLAttributes,
  TextareaHTMLAttributes,
  useId,
} from "react";
import Link from "next/link";
import styles from "./ui.module.css";

function cx(...classes: Array<string | false | null | undefined>) {
  return classes.filter(Boolean).join(" ");
}

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "secondary" | "danger";
  fullWidth?: boolean;
};

export function Button({ variant = "primary", fullWidth = false, className, ...props }: ButtonProps) {
  return (
    <button
      className={cx(styles.button, styles[variant], fullWidth && styles.fullWidth, className)}
      {...props}
    />
  );
}

type FieldShellProps = {
  id?: string;
  label: ReactNode;
  required?: boolean;
  error?: string;
  helpText?: ReactNode;
  className?: string;
  children: (fieldProps: {
    id: string;
    describedBy?: string;
    invalid: boolean;
  }) => ReactNode;
};

function FieldShell({ id, label, required, error, helpText, className, children }: FieldShellProps) {
  const generatedId = useId();
  const fieldId = id ?? generatedId;
  const errorId = `${fieldId}-error`;
  const helpId = `${fieldId}-help`;
  const describedBy = [error ? errorId : null, helpText ? helpId : null].filter(Boolean).join(" ") || undefined;

  return (
    <div className={cx(styles.field, className)}>
      <label className={styles.label} htmlFor={fieldId}>
        {label} {required && <span className={styles.required}>*</span>}
      </label>
      {children({ id: fieldId, describedBy, invalid: Boolean(error) })}
      {error && (
        <p id={errorId} className={styles.errorText}>
          {error}
        </p>
      )}
      {helpText && (
        <span id={helpId} className={styles.helpText}>
          {helpText}
        </span>
      )}
    </div>
  );
}

type TextFieldProps = Omit<InputHTMLAttributes<HTMLInputElement>, "id"> & {
  id?: string;
  label: ReactNode;
  error?: string;
  helpText?: ReactNode;
  trailingIcon?: ReactNode;
  fieldClassName?: string;
};

export function TextField({
  id,
  label,
  error,
  helpText,
  trailingIcon,
  fieldClassName,
  className,
  required,
  ...props
}: TextFieldProps) {
  const hasTrailingIcon = Boolean(trailingIcon);

  return (
    <FieldShell id={id} label={label} required={required} error={error} helpText={helpText} className={fieldClassName}>
      {({ id: fieldId, describedBy, invalid }) => (
        <div className={styles.controlWrap}>
          <input
            id={fieldId}
            className={cx(styles.control, hasTrailingIcon && styles.withTrailing, className)}
            required={required}
            aria-invalid={invalid}
            aria-describedby={describedBy}
            {...props}
          />
          {trailingIcon && <span className={styles.trailing}>{trailingIcon}</span>}
        </div>
      )}
    </FieldShell>
  );
}

type SelectFieldProps = Omit<SelectHTMLAttributes<HTMLSelectElement>, "id"> & {
  id?: string;
  label: ReactNode;
  error?: string;
  helpText?: ReactNode;
  fieldClassName?: string;
};

export function SelectField({
  id,
  label,
  error,
  helpText,
  fieldClassName,
  className,
  required,
  children,
  ...props
}: SelectFieldProps) {
  return (
    <FieldShell id={id} label={label} required={required} error={error} helpText={helpText} className={fieldClassName}>
      {({ id: fieldId, describedBy, invalid }) => (
        <select
          id={fieldId}
          className={cx(styles.control, className)}
          required={required}
          aria-invalid={invalid}
          aria-describedby={describedBy}
          {...props}
        >
          {children}
        </select>
      )}
    </FieldShell>
  );
}

type TextareaFieldProps = Omit<TextareaHTMLAttributes<HTMLTextAreaElement>, "id"> & {
  id?: string;
  label: ReactNode;
  error?: string;
  helpText?: ReactNode;
  fieldClassName?: string;
};

export function TextareaField({
  id,
  label,
  error,
  helpText,
  fieldClassName,
  className,
  required,
  ...props
}: TextareaFieldProps) {
  return (
    <FieldShell id={id} label={label} required={required} error={error} helpText={helpText} className={fieldClassName}>
      {({ id: fieldId, describedBy, invalid }) => (
        <textarea
          id={fieldId}
          className={cx(styles.control, styles.textarea, className)}
          required={required}
          aria-invalid={invalid}
          aria-describedby={describedBy}
          {...props}
        />
      )}
    </FieldShell>
  );
}

type CardProps = {
  title?: ReactNode;
  subtitle?: ReactNode;
  children: ReactNode;
  className?: string;
};

export function Card({ title, subtitle, children, className }: CardProps) {
  return (
    <div className={cx(styles.card, className)}>
      {(title || subtitle) && (
        <div className={styles.cardHeader}>
          {title && <h2 className={styles.cardTitle}>{title}</h2>}
          {subtitle && <p className={styles.cardSubtitle}>{subtitle}</p>}
        </div>
      )}
      {children}
    </div>
  );
}

type BadgeProps = {
  children: ReactNode;
  variant?: "success" | "danger" | "warning" | "neutral" | "brand";
  className?: string;
};

export function Badge({ children, variant = "neutral", className }: BadgeProps) {
  const variantClass = {
    success: styles.badgeSuccess,
    danger: styles.badgeDanger,
    warning: styles.badgeWarning,
    neutral: styles.badgeNeutral,
    brand: styles.badgeBrand,
  }[variant];

  return <span className={cx(styles.badge, variantClass, className)}>{children}</span>;
}

export function FormActions({ children, className }: { children: ReactNode; className?: string }) {
  return <div className={cx(styles.formActions, className)}>{children}</div>;
}

export function StatusBadge({
  status,
  children,
  className,
}: {
  status: "success" | "danger" | "warning" | "neutral" | "brand";
  children: ReactNode;
  className?: string;
}) {
  return (
    <Badge variant={status} className={className}>
      {children}
    </Badge>
  );
}

export function PageHeader({
  eyebrow,
  title,
  subtitle,
  actions,
}: {
  eyebrow?: ReactNode;
  title: ReactNode;
  subtitle?: ReactNode;
  actions?: ReactNode;
}) {
  return (
    <div className={styles.pageHeader}>
      <div>
        {eyebrow && <span className={styles.eyebrow}>{eyebrow}</span>}
        <h1 className={styles.pageTitle}>{title}</h1>
        {subtitle && <p className={styles.pageSubtitle}>{subtitle}</p>}
      </div>
      {actions && <div className={styles.headerActions}>{actions}</div>}
    </div>
  );
}

export function MetricCard({
  label,
  value,
  trend,
}: {
  label: ReactNode;
  value: ReactNode;
  trend?: ReactNode;
}) {
  return (
    <div className={styles.metricCard}>
      <span className={styles.metricLabel}>{label}</span>
      <strong className={styles.metricValue}>{value}</strong>
      {trend && <div className={styles.metricTrend}>{trend}</div>}
    </div>
  );
}

export function SearchBar({
  value,
  onChange,
  placeholder = "Buscar...",
  className,
}: {
  value: string;
  onChange: (value: string) => void;
  placeholder?: string;
  className?: string;
}) {
  return (
    <div className={cx(styles.searchWrap, className)}>
      <span className={styles.searchIcon}>⌕</span>
      <input
        className={styles.searchInput}
        value={value}
        placeholder={placeholder}
        onChange={(event) => onChange(event.target.value)}
      />
    </div>
  );
}

export function FilterTabs<T extends string>({
  items,
  value,
  onChange,
}: {
  items: Array<{ value: T; label: ReactNode }>;
  value: T;
  onChange: (value: T) => void;
}) {
  return (
    <div className={styles.filterTabs}>
      {items.map((item) => (
        <button
          key={item.value}
          type="button"
          className={cx(styles.filterTab, value === item.value && styles.filterTabActive)}
          onClick={() => onChange(item.value)}
        >
          {item.label}
        </button>
      ))}
    </div>
  );
}

export function EmptyState({
  title,
  description,
  action,
}: {
  title: ReactNode;
  description: ReactNode;
  action?: ReactNode;
}) {
  return (
    <div className={styles.emptyState}>
      <div className={styles.emptyIcon}>!</div>
      <h2 className={styles.emptyTitle}>{title}</h2>
      <p className={styles.emptyText}>{description}</p>
      {action}
    </div>
  );
}

function formatEventDate(value?: string) {
  if (!value) return { day: "--", month: "---" };
  const date = new Date(`${value}T00:00:00`);
  if (Number.isNaN(date.getTime())) return { day: "--", month: "---" };
  return {
    day: String(date.getDate()).padStart(2, "0"),
    month: date.toLocaleDateString("pt-BR", { month: "short" }).replace(".", "").toUpperCase(),
  };
}

export function EventCard({
  href,
  title,
  description,
  date,
  location,
  capacity,
  status,
  footer,
  tone = 0,
}: {
  href: string;
  title: string;
  description?: string;
  date?: string;
  location?: string;
  capacity?: ReactNode;
  status?: ReactNode;
  footer?: ReactNode;
  tone?: number;
}) {
  const dateParts = formatEventDate(date);
  const coverTone = [null, styles.eventCoverAlt1, styles.eventCoverAlt2, styles.eventCoverAlt3][tone % 4];

  return (
    <Link href={href} className={styles.eventCard}>
      <div className={cx(styles.eventCover, coverTone)}>
        <span className={styles.eventCategory}>{status ?? "Evento"}</span>
        <div className={styles.eventDate}>
          {dateParts.day}
          <small>{dateParts.month}</small>
        </div>
      </div>
      <div className={styles.eventBody}>
        <h3 className={styles.eventTitle}>{title}</h3>
        {description && <p className={styles.eventDesc}>{description}</p>}
        <div className={styles.meta}>
          {location && <span>Local: {location}</span>}
          {date && <span>Data: {dateParts.day}/{date.slice(5, 7)}/{date.slice(0, 4)}</span>}
          {capacity && <span>Capacidade: {capacity}</span>}
        </div>
        {footer && <div className={styles.eventFooter}>{footer}</div>}
      </div>
    </Link>
  );
}

export function TicketCard({
  title,
  subtitle,
  status,
  details,
  action,
  locked = false,
}: {
  title: ReactNode;
  subtitle?: ReactNode;
  status: ReactNode;
  details?: ReactNode;
  action?: ReactNode;
  locked?: boolean;
}) {
  return (
    <article className={styles.ticketCard}>
      <div className={styles.ticketInfo}>
        {status}
        <h3 className={styles.ticketTitle}>{title}</h3>
        {subtitle && <p className={styles.ticketText}>{subtitle}</p>}
        {details && <div className={styles.meta}>{details}</div>}
        {action}
      </div>
      <div className={styles.qr}>
        <div>
          <div className={styles.qrBox} style={locked ? { filter: "blur(2px)", opacity: 0.45 } : undefined} />
          <p className={styles.qrText}>{locked ? "Bloqueado" : "QR liberado"}</p>
        </div>
      </div>
    </article>
  );
}
